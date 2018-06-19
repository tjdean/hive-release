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

package org.apache.hive.service.server;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.session.KillQuery;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.OperationHandle;
import org.apache.hive.service.cli.operation.Operation;
import org.apache.hive.service.cli.operation.OperationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.ApplicationsRequestScope;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationsResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.client.ClientRMProxy;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KillQueryImpl implements KillQuery {
  private final static Logger LOG = LoggerFactory.getLogger(KillQueryImpl.class);

  private final OperationManager operationManager;

  public KillQueryImpl(OperationManager operationManager) {
    this.operationManager = operationManager;
  }

  @Override
  public void killQuery(String queryId) throws HiveException {
    try {
      Operation operation = operationManager.getOperationByQueryId(queryId);
      if (operation == null) {
        LOG.info("Query not found: " + queryId);
      } else {
        OperationHandle handle = operation.getHandle();
        operationManager.cancelOperation(handle);
      }
      killChildYarnJobs(SessionState.getSessionConf(), queryId);
    } catch (HiveSQLException e) {
      throw new HiveException(e);
    }
  }

  public static Set<ApplicationId> getChildYarnJobs(Configuration conf, String tag) {
    Set<ApplicationId> childYarnJobs = new HashSet<ApplicationId>();
    GetApplicationsRequest gar = GetApplicationsRequest.newInstance();
    gar.setScope(ApplicationsRequestScope.OWN);
    gar.setApplicationTags(Collections.singleton(tag));

    try {
      ApplicationClientProtocol proxy = ClientRMProxy.createRMProxy(conf, ApplicationClientProtocol.class);
      GetApplicationsResponse apps = proxy.getApplications(gar);
      List<ApplicationReport> appsList = apps.getApplicationList();
      for(ApplicationReport appReport : appsList) {
        childYarnJobs.add(appReport.getApplicationId());
      }
    } catch (YarnException | IOException ioe) {
      throw new RuntimeException("Exception occurred while finding child jobs", ioe);
    }

    if (childYarnJobs.isEmpty()) {
      LOG.info("No child applications found");
    } else {
      LOG.info("Found child YARN applications: " + StringUtils.join(childYarnJobs, ","));
    }

    return childYarnJobs;
  }

  public static void killChildYarnJobs(Configuration conf, String tag) {
    try {
      Set<ApplicationId> childYarnJobs = getChildYarnJobs(conf, tag);
      if (!childYarnJobs.isEmpty()) {
        YarnClient yarnClient = YarnClient.createYarnClient();
        yarnClient.init(conf);
        yarnClient.start();
        for (ApplicationId app : childYarnJobs) {
          yarnClient.killApplication(app);
        }
      }
    } catch (IOException | YarnException ye) {
      throw new RuntimeException("Exception occurred while killing child job(s)", ye);
    }
  }
}
