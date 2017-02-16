/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.ql.hooks;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.conf.HiveConf.ConfVars;
import org.apache.hadoop.hive.ql.QueryPlan;
import org.apache.hadoop.hive.ql.exec.ExplainTask;
import org.apache.hadoop.hive.ql.exec.Task;
import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hadoop.hive.ql.exec.tez.TezTask;
import org.apache.hadoop.hive.ql.log.PerfLogger;
import org.apache.hadoop.hive.ql.plan.ExplainWork;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEvent;
import org.apache.hadoop.yarn.client.api.TimelineClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hive.common.util.ShutdownHookManager;
import org.json.JSONObject;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * ATSHook sends query + plan info to Yarn App Timeline Server. To enable (hadoop 2.4 and up) set
 * hive.exec.pre.hooks/hive.exec.post.hooks/hive.exec.failure.hooks to include this class.
 */
public class ATSHook implements ExecuteWithHookContext {

  private static final Log LOG = LogFactory.getLog(ATSHook.class.getName());
  private static final Object LOCK = new Object();
  private static final int VERSION = 2;
  private static ExecutorService executor;
  private static TimelineClient timelineClient;
  private enum EntityTypes { HIVE_QUERY_ID };
  private enum EventTypes { QUERY_SUBMITTED, QUERY_COMPLETED };

  private enum OtherInfoTypes {
    QUERY, STATUS, TEZ, MAPRED, INVOKER_INFO, SESSION_ID, THREAD_NAME, LOG_TRACE_ID, VERSION,
    CLIENT_IP_ADDRESS, HIVE_ADDRESS, HIVE_INSTANCE_TYPE, CONF, PERF,
  };
  private enum ExecutionMode {
    MR, TEZ, SPARK, NONE
  };
  private enum PrimaryFilterTypes {
    user, requestuser, operationid, executionmode, tablesread, tableswritten, queue
  };

  private static final int WAIT_TIME = 3;

  public ATSHook() {
    synchronized(LOCK) {
      if (executor == null) {

        executor = Executors.newSingleThreadExecutor(
           new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ATS Logger %d").build());

        YarnConfiguration yarnConf = new YarnConfiguration();
        timelineClient = TimelineClient.createTimelineClient();
        timelineClient.init(yarnConf);
        timelineClient.start();

        ShutdownHookManager.addShutdownHook(new Runnable() {
          @Override
          public void run() {
            try {
              executor.shutdown();
              executor.awaitTermination(WAIT_TIME, TimeUnit.SECONDS);
              executor = null;
            } catch(InterruptedException ie) { /* ignore */ }
            timelineClient.stop();
          }
        });
      }
    }

    LOG.info("Created ATS Hook");
  }

  @Override
  public void run(final HookContext hookContext) throws Exception {
    final long currentTime = System.currentTimeMillis();
    final HiveConf conf = new HiveConf(hookContext.getConf());

    final Map<String, Long> durations = new HashMap<String, Long>();
    for (String key : hookContext.getPerfLogger().getEndTimes().keySet()) {
      durations.put(key, hookContext.getPerfLogger().getDuration(key));
    }

    executor.submit(new Runnable() {
        @Override
        public void run() {
          try {
            QueryPlan plan = hookContext.getQueryPlan();
            if (plan == null) {
              return;
            }
            String queryId = plan.getQueryId();
            String opId = hookContext.getOperationId();
            long queryStartTime = plan.getQueryStartTime();
            String user = hookContext.getUgi().getUserName();
            String requestuser = hookContext.getUserName();
            if (hookContext.getUserName() == null ){
              requestuser = hookContext.getUgi().getUserName() ;
            }
            int numMrJobs = Utilities.getMRTasks(plan.getRootTasks()).size();
            int numTezJobs = Utilities.getTezTasks(plan.getRootTasks()).size();
            if (numMrJobs + numTezJobs <= 0) {
              return; // ignore client only queries
            }

            switch(hookContext.getHookType()) {
            case PRE_EXEC_HOOK:
              ExplainTask explain = new ExplainTask();
              explain.initialize(conf, plan, null);
              String query = plan.getQueryStr();
              List<Task<?>> rootTasks = plan.getRootTasks();
              JSONObject explainPlan = explain.getJSONPlan(null, null, rootTasks,
                   plan.getFetchTask(), true, false, false);
              //String logID = conf.getLogIdVar(hookContext.getSessionId());
              String sessionID = hookContext.getSessionId();
              List<String> tablesRead = getTablesFromEntitySet(hookContext.getInputs());
              List<String> tablesWritten = getTablesFromEntitySet(hookContext.getOutputs());
              String executionMode = getExecutionMode(plan).name();
              String hiveInstanceAddress = hookContext.getHiveInstanceAddress();
              if (hiveInstanceAddress == null) {
                hiveInstanceAddress = InetAddress.getLocalHost().getHostAddress();
              }
              String hiveInstanceType = hookContext.isHiveServerQuery() ? "HS2" : "CLI";
              fireAndForget(conf,
                  createPreHookEvent(queryId, query, explainPlan, queryStartTime,
                      user, requestuser, numMrJobs, numTezJobs, opId,
                      hookContext.getIpAddress(), hiveInstanceAddress, hiveInstanceType,
                      sessionID/*logID*/, hookContext.getThreadId(), plan.getUserProvidedContext(), executionMode,
                      tablesRead, tablesWritten, conf));
              break;
            case POST_EXEC_HOOK:
              fireAndForget(conf, createPostHookEvent(queryId, currentTime, user, requestuser, true, opId, durations));
              break;
            case ON_FAILURE_HOOK:
              fireAndForget(conf, createPostHookEvent(queryId, currentTime, user, requestuser , false, opId, durations));
              break;
            default:
              //ignore
              break;
            }
          } catch (Exception e) {
            LOG.info("Failed to submit plan to ATS: " + StringUtils.stringifyException(e));
          }
        }
      });
  }

  protected List<String> getTablesFromEntitySet(Set<? extends Entity> entities) {
    List<String> tableNames = new ArrayList<String>();
    for (Entity entity : entities) {
      if (entity.getType() == Entity.Type.TABLE) {
        tableNames.add(entity.getTable().getDbName() + "." + entity.getTable().getTableName());
      }
    }
    return tableNames;
  }

  protected ExecutionMode getExecutionMode(QueryPlan plan) {
    int numMRJobs = Utilities.getMRTasks(plan.getRootTasks()).size();
    int numSparkJobs = Utilities.getSparkTasks(plan.getRootTasks()).size();
    int numTezJobs = Utilities.getTezTasks(plan.getRootTasks()).size();

    ExecutionMode mode = ExecutionMode.MR;
    if (0 == (numMRJobs + numSparkJobs + numTezJobs)) {
      mode = ExecutionMode.NONE;
    } else if (numSparkJobs > 0) {
      return ExecutionMode.SPARK;
    } else if (numTezJobs > 0) {
      mode = ExecutionMode.TEZ;
    }

    return mode;
  }

  TimelineEntity createPreHookEvent(String queryId, String query, JSONObject explainPlan,
      long startTime, String user, String requestuser, int numMrJobs, int numTezJobs, String opId,
      String clientIpAddress, String hiveInstanceAddress, String hiveInstanceType,
      String sessionID /*String logID*/, String threadId, String logTraceId, String executionMode,
      List<String> tablesRead, List<String> tablesWritten, HiveConf conf) throws Exception {

    JSONObject queryObj = new JSONObject();
    queryObj.put("queryText", query);
    queryObj.put("queryPlan", explainPlan);

    LOG.info("Received pre-hook notification for :" + queryId);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Otherinfo: " + queryObj.toString());
      LOG.debug("Operation id: <" + opId + ">");
    }

    conf.stripHiddenConfigurations(conf);
    Map<String, String> confMap = new HashMap<String, String>();
    for (Map.Entry<String, String> setting : conf) {
      confMap.put(setting.getKey(), setting.getValue());
    }
    JSONObject confObj = new JSONObject((Map) confMap);

    TimelineEntity atsEntity = new TimelineEntity();
    atsEntity.setEntityId(queryId);
    atsEntity.setEntityType(EntityTypes.HIVE_QUERY_ID.name());
    atsEntity.addPrimaryFilter(PrimaryFilterTypes.user.name(), user);
    atsEntity.addPrimaryFilter(PrimaryFilterTypes.requestuser.name(), requestuser);
    atsEntity.addPrimaryFilter(PrimaryFilterTypes.executionmode.name(), executionMode);
    atsEntity.addPrimaryFilter(PrimaryFilterTypes.queue.name(), conf.get("mapreduce.job.queuename"));

    if (opId != null) {
      atsEntity.addPrimaryFilter(PrimaryFilterTypes.operationid.name(), opId);
    }

    for (String tabName : tablesRead) {
      atsEntity.addPrimaryFilter(PrimaryFilterTypes.tablesread.name(), tabName);
    }
    for (String tabName : tablesWritten) {
      atsEntity.addPrimaryFilter(PrimaryFilterTypes.tableswritten.name(), tabName);
    }

    TimelineEvent startEvt = new TimelineEvent();
    startEvt.setEventType(EventTypes.QUERY_SUBMITTED.name());
    startEvt.setTimestamp(startTime);
    atsEntity.addEvent(startEvt);

    atsEntity.addOtherInfo(OtherInfoTypes.QUERY.name(), queryObj.toString());
    atsEntity.addOtherInfo(OtherInfoTypes.TEZ.name(), numTezJobs > 0);
    atsEntity.addOtherInfo(OtherInfoTypes.MAPRED.name(), numMrJobs > 0);
    atsEntity.addOtherInfo(OtherInfoTypes.SESSION_ID.name(), sessionID);
//    atsEntity.addOtherInfo(OtherInfoTypes.INVOKER_INFO.name(), logID);
    atsEntity.addOtherInfo(OtherInfoTypes.THREAD_NAME.name(), threadId);
    atsEntity.addOtherInfo(OtherInfoTypes.VERSION.name(), VERSION);
    if (clientIpAddress != null) {
      atsEntity.addOtherInfo(OtherInfoTypes.CLIENT_IP_ADDRESS.name(), clientIpAddress);
    }
    atsEntity.addOtherInfo(OtherInfoTypes.HIVE_ADDRESS.name(), hiveInstanceAddress);
    atsEntity.addOtherInfo(OtherInfoTypes.HIVE_INSTANCE_TYPE.name(), hiveInstanceType);
    atsEntity.addOtherInfo(OtherInfoTypes.CONF.name(), confObj.toString());
    if ((logTraceId != null) && (logTraceId.equals("") == false)) {
      atsEntity.addOtherInfo(OtherInfoTypes.LOG_TRACE_ID.name(), logTraceId);
    }

    return atsEntity;
  }

  TimelineEntity createPostHookEvent(String queryId, long stopTime, String user, String requestuser, boolean success,
      String opId, Map<String, Long> durations) throws Exception {
    LOG.info("Received post-hook notification for :" + queryId);

    TimelineEntity atsEntity = new TimelineEntity();
    atsEntity.setEntityId(queryId);
    atsEntity.setEntityType(EntityTypes.HIVE_QUERY_ID.name());
    atsEntity.addPrimaryFilter(PrimaryFilterTypes.user.name(), user);
    atsEntity.addPrimaryFilter(PrimaryFilterTypes.requestuser.name(), requestuser);
    if (opId != null) {
      atsEntity.addPrimaryFilter(PrimaryFilterTypes.operationid.name(), opId);
    }

    TimelineEvent stopEvt = new TimelineEvent();
    stopEvt.setEventType(EventTypes.QUERY_COMPLETED.name());
    stopEvt.setTimestamp(stopTime);
    atsEntity.addEvent(stopEvt);

    atsEntity.addOtherInfo(OtherInfoTypes.STATUS.name(), success);

    // Perf times
    JSONObject perfObj = new JSONObject(new LinkedHashMap<>());
    for (Map.Entry<String, Long> entry : durations.entrySet()) {
      perfObj.put(entry.getKey(), entry.getValue());
    }
    atsEntity.addOtherInfo(OtherInfoTypes.PERF.name(), perfObj.toString());

    return atsEntity;
  }

  synchronized void fireAndForget(Configuration conf, TimelineEntity entity) throws Exception {
    timelineClient.putEntities(entity);
  }
}
