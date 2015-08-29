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

package org.apache.hadoop.hive.metastore;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.conf.HiveConf.ConfVars;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.thrift.transport.TTransportException;


public class TestRemoteHiveMetaStore extends TestHiveMetaStore {
  private static boolean isServerStarted = false;

  public TestRemoteHiveMetaStore() {
    super();
    isThriftClient = true;
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    if (isServerStarted) {
      assertNotNull("Unable to connect to the MetaStore server", client);
      return;
    }

    int port = MetaStoreUtils.findFreePort();
    System.out.println("Starting MetaStore Server on port " + port);
    MetaStoreUtils.startMetaStore(port, ShimLoader.getHadoopThriftAuthBridge());
    isServerStarted = true;

    // This is default case with setugi off for both client and server
    createClient(false, port);
  }

  protected void createClient(boolean setugi, int port) throws Exception {
    hiveConf.setVar(HiveConf.ConfVars.METASTOREURIS, "thrift://localhost:" + port);
    hiveConf.setBoolVar(ConfVars.METASTORE_EXECUTE_SET_UGI,setugi);
    client = new HiveMetaStoreClient(hiveConf);
  }


  // Metastore Client to simulate failed metastore requests
  static class FailingHiveMetaStoreClient extends HiveMetaStoreClient implements IMetaStoreClient {

    static int configuredFailures;
    static int callCount;

    public static int getConfiguredFailures() {
      return configuredFailures;
    }

    public static void setConfiguredFailures(int newConfiguredFailures) {
      configuredFailures = newConfiguredFailures;
    }

    public static int getCallCount() {
      return callCount;
    }

    public static void setCallCount(int newCallCount) {
      callCount = callCount;
    }

    public FailingHiveMetaStoreClient(HiveConf conf) throws MetaException {
      super(conf);
    }

    public FailingHiveMetaStoreClient(HiveConf conf, HiveMetaHookLoader hookLoader) throws MetaException {
      super(conf, hookLoader);
    }

    /**
     * Override getAllDatabases() to fail a specified number of times, using configuredFailures
     */
    @Override
    public List<String> getAllDatabases() throws MetaException {
      ++callCount;
      if (configuredFailures > 0 && configuredFailures >= callCount) {
        String msg = "Test failure " + callCount;
        Exception cause = new TTransportException(msg);
        MetaException error = new MetaException(ExceptionUtils.getStackTrace(cause));
        throw error;
      }

      return super.getAllDatabases();
    }
  }

  private HiveMetaHookLoader getHookLoader() {
    HiveMetaHookLoader hookLoader = new HiveMetaHookLoader() {
      @Override
      public HiveMetaHook getHook(
          org.apache.hadoop.hive.metastore.api.Table tbl)
          throws MetaException {
        return null;
      }
    };
    return hookLoader;
  }

  public void testRetryTTransportException() throws Throwable {
    HiveConf conf = new HiveConf(hiveConf);
    // Test a normal retriable client
    IMetaStoreClient client =
        RetryingMetaStoreClient.getProxy(conf, getHookLoader(), HiveMetaStoreClient.class.getName());
    List<String> dbs = client.getAllDatabases();
    int dbCount = dbs.size();
    client.close();

    FailingHiveMetaStoreClient.setConfiguredFailures(1);
    FailingHiveMetaStoreClient.setCallCount(0);

    // Now try with our failing client, set to fail for the first call to getAllDatabases()
    client = RetryingMetaStoreClient.getProxy(conf, getHookLoader(), FailingHiveMetaStoreClient.class.getName());
    dbs = client.getAllDatabases();
    assertEquals(dbCount, dbs.size());
    assertEquals(2, FailingHiveMetaStoreClient.getCallCount());
  }
}
