/*
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
import org.apache.hadoop.hive.metastore.api.Function;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.NotificationEventRequest;
import org.apache.hadoop.hive.metastore.api.NotificationEventResponse;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;

import static org.junit.Assert.assertEquals;


/**
 * A wrapper around {@link ObjectStore} that allows us to inject custom behaviour
 * on to some of the methods for testing.
 */
public class InjectableBehaviourObjectStore extends ObjectStore {
  public InjectableBehaviourObjectStore() {
    super();
  }

  /**
   * A utility class that allows people injecting behaviour to determine if their injections occurred.
   */
  public static abstract class BehaviourInjection<T, F>
      implements com.google.common.base.Function<T, F>{
    protected boolean injectionPathCalled = false;
    protected boolean nonInjectedPathCalled = false;

    public void assertInjectionsPerformed(
        boolean expectedInjectionCalled, boolean expectedNonInjectedPathCalled){
      assertEquals(expectedInjectionCalled, injectionPathCalled);
      assertEquals(expectedNonInjectedPathCalled, nonInjectedPathCalled);
    }
  }

  /**
   * A utility class to pass the arguments of the caller to the stub method.
   */
  public class CallerArguments {
    public String dbName;
    public String tblName;
    public List<String> ptnValues;
    public String funcName;

    public CallerArguments(String dbName) {
      this.dbName = dbName;
    }
  }

  private static com.google.common.base.Function<Table, Table> getTableModifier =
      com.google.common.base.Functions.identity();
  private static com.google.common.base.Function<Partition, Partition> getPartitionModifier =
          com.google.common.base.Functions.identity();
  private static com.google.common.base.Function<List<String>, List<String>> listPartitionNamesModifier =
          com.google.common.base.Functions.identity();
  private static com.google.common.base.Function<NotificationEventResponse, NotificationEventResponse>
          getNextNotificationModifier = com.google.common.base.Functions.identity();

  private static com.google.common.base.Function<CallerArguments, Boolean> callerVerifier = null;

  // Methods to set/reset getTable modifier
  public static void setGetTableBehaviour(com.google.common.base.Function<Table, Table> modifier){
    if (modifier == null) {
      getTableModifier = com.google.common.base.Functions.identity();
    } else {
      getTableModifier = modifier;
    }
  }

  public static void resetGetTableBehaviour(){
    setGetTableBehaviour(null);
  }

  // Methods to set/reset getPartition modifier
  public static void setGetPartitionBehaviour(com.google.common.base.Function<Partition, Partition> modifier){
    if (modifier == null) {
      getPartitionModifier = com.google.common.base.Functions.identity();
    } else {
      getPartitionModifier = modifier;
    }
  }

  public static void resetGetPartitionBehaviour(){
    setGetPartitionBehaviour(null);
  }

  // Methods to set/reset listPartitionNames modifier
  public static void setListPartitionNamesBehaviour(com.google.common.base.Function<List<String>, List<String>> modifier){
    listPartitionNamesModifier = (modifier == null)? com.google.common.base.Functions.identity() : modifier;
  }

  public static void resetListPartitionNamesBehaviour(){
    setListPartitionNamesBehaviour(null);
  }

  // Methods to set/reset getNextNotification modifier
  public static void setGetNextNotificationBehaviour(
          com.google.common.base.Function<NotificationEventResponse,NotificationEventResponse> modifier){
    getNextNotificationModifier = (modifier == null)? com.google.common.base.Functions.identity() : modifier;
  }

  public static void resetGetNextNotificationBehaviour(){
    setGetNextNotificationBehaviour(null);
  }

  // Methods to set/reset caller checker
  public static void setCallerVerifier(com.google.common.base.Function<CallerArguments, Boolean> verifier){
    callerVerifier = verifier;
  }

  public static void resetCallerVerifier(){
    setCallerVerifier(null);
  }

  // ObjectStore methods to be overridden with injected behavior
  @Override
  public Table getTable(String dbName, String tableName) throws MetaException {
    return getTableModifier.apply(super.getTable(dbName, tableName));
  }

  @Override
  public Partition getPartition(String dbName, String tableName,
                                List<String> partVals) throws NoSuchObjectException, MetaException {
    return getPartitionModifier.apply(super.getPartition(dbName, tableName, partVals));
  }

  @Override
  public List<String> listPartitionNames(String dbName, String tableName, short max) throws MetaException {
    return listPartitionNamesModifier.apply(super.listPartitionNames(dbName, tableName, max));
  }

  @Override
  public NotificationEventResponse getNextNotification(NotificationEventRequest rqst) {
    return getNextNotificationModifier.apply(super.getNextNotification(rqst));
  }

  @Override
  public void createTable(Table tbl) throws InvalidObjectException, MetaException {
    if (callerVerifier != null) {
      CallerArguments args = new CallerArguments(tbl.getDbName());
      args.tblName = tbl.getTableName();
      Boolean success = callerVerifier.apply(args);
      if ((success != null) && !success) {
        throw new MetaException("InjectableBehaviourObjectStore: Invalid Create Table operation on DB: "
                + args.dbName + " table: " + args.tblName);
      }
    }
    super.createTable(tbl);
  }

  @Override
  public void createFunction(Function func) throws InvalidObjectException, MetaException {
    if (callerVerifier != null) {
      CallerArguments args = new CallerArguments(func.getDbName());
      args.funcName = func.getFunctionName();
      Boolean success = callerVerifier.apply(args);
      if ((success != null) && !success) {
        throw new MetaException("InjectableBehaviourObjectStore: Invalid Create Function operation on DB: "
                + args.dbName + " function: " + args.funcName);
      }
    }
    super.createFunction(func);
  }

  @Override
  public Partition getPartitionWithAuth(String dbName, String tblName,
                                        List<String> partVals, String userName, List<String> groupNames)
          throws MetaException, NoSuchObjectException, InvalidObjectException {
    if (callerVerifier != null) {
      CallerArguments args = new CallerArguments(dbName);
      args.tblName = tblName;
      args.ptnValues = partVals;
      Boolean success = callerVerifier.apply(args);
      if ((success != null) && !success) {
        throw new MetaException("InjectableBehaviourObjectStore: Invalid Get Partition operation on DB: "
                + args.dbName + " table: " + args.tblName);
      }
    }
    return super.getPartitionWithAuth(dbName, tblName, partVals, userName, groupNames);
  }

}
