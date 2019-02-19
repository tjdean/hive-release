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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.hadoop.hive.metastore.messaging;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.ColumnStatistics;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.Function;
import org.apache.hadoop.hive.metastore.api.Index;
import org.apache.hadoop.hive.metastore.api.NotificationEvent;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.messaging.json.JSONAddPartitionMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONAlterDatabaseMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONAlterIndexMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONAlterPartitionMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONAlterTableMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONCreateDatabaseMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONCreateFunctionMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONCreateIndexMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONCreateTableMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONDropDatabaseMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONDropFunctionMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONDropIndexMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONDropPartitionMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONDropTableMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONInsertMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONUpdatePartitionColumnStatMessage;
import org.apache.hadoop.hive.metastore.messaging.json.JSONUpdateTableColumnStatMessage;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TJSONProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBuilder {
  private static final Logger LOG = LoggerFactory.getLogger(MessageBuilder.class);

  public static final String ADD_PARTITION_EVENT = "ADD_PARTITION";
  public static final String ALTER_PARTITION_EVENT = "ALTER_PARTITION";
  public static final String DROP_PARTITION_EVENT = "DROP_PARTITION";
  public static final String CREATE_TABLE_EVENT = "CREATE_TABLE";
  public static final String ALTER_TABLE_EVENT = "ALTER_TABLE";
  public static final String DROP_TABLE_EVENT = "DROP_TABLE";
  public static final String CREATE_DATABASE_EVENT = "CREATE_DATABASE";
  public static final String ALTER_DATABASE_EVENT = "ALTER_DATABASE";
  public static final String DROP_DATABASE_EVENT = "DROP_DATABASE";
  public static final String CREATE_INDEX_EVENT = "CREATE_INDEX";
  public static final String ALTER_INDEX_EVENT = "ALTER_INDEX";
  public static final String DROP_INDEX_EVENT = "DROP_INDEX";
  public static final String INSERT_EVENT = "INSERT";
  public static final String CREATE_FUNCTION_EVENT = "CREATE_FUNCTION";
  public static final String DROP_FUNCTION_EVENT = "DROP_FUNCTION";
  public static final String UPDATE_TBL_COL_STAT_EVENT = "UPDATE_TBL_COL_STAT_EVENT";
  public static final String UPDATE_PART_COL_STAT_EVENT = "UPDATE_PART_COL_STAT_EVENT";

  protected static final Configuration conf = new HiveConf();

  private static final String MS_SERVER_URL = HiveConf
      .getVar(conf, HiveConf.ConfVars.METASTOREURIS, "");
  private static final String MS_SERVICE_PRINCIPAL =
      HiveConf.getVar(conf, HiveConf.ConfVars.METASTORE_KERBEROS_PRINCIPAL, "");

  private static volatile MessageBuilder instance;
  private static final Object lock = new Object();

  public static MessageBuilder getInstance() {
    if (instance == null) {
      synchronized (lock) {
        if (instance == null) {
          instance = new MessageBuilder();
        }
      }
    }
    return instance;
  }


  public CreateDatabaseMessage buildCreateDatabaseMessage(Database db) {
    return new JSONCreateDatabaseMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL, db, now());
  }

  public AlterDatabaseMessage buildAlterDatabaseMessage(Database beforeDb, Database afterDb) {
    return new JSONAlterDatabaseMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL,
        beforeDb, afterDb, now());
  }

  public DropDatabaseMessage buildDropDatabaseMessage(Database db) {
    return new JSONDropDatabaseMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL, db.getName(), now());
  }

  public CreateTableMessage buildCreateTableMessage(Table table, Iterator<String> fileIter) {
    return new JSONCreateTableMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL, table, fileIter, now());
  }

  public AlterTableMessage buildAlterTableMessage(Table before, Table after, boolean isTruncateOp) {
    return new JSONAlterTableMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL,
        before, after, isTruncateOp, now());
  }

  public DropTableMessage buildDropTableMessage(Table table) {
    return new JSONDropTableMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL, table, now());
  }

  public AddPartitionMessage buildAddPartitionMessage(Table table,
      Iterator<Partition> partitionsIterator, Iterator<PartitionFiles> partitionFileIter) {
    return new JSONAddPartitionMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL, table,
        partitionsIterator, partitionFileIter, now());
  }

  public AlterPartitionMessage buildAlterPartitionMessage(Table table, Partition before,
      Partition after, boolean isTruncateOp) {
    return new JSONAlterPartitionMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL,
        table, before, after, isTruncateOp, now());
  }

  public DropPartitionMessage buildDropPartitionMessage(Table table,
      Iterator<Partition> partitionsIterator) {
    return new JSONDropPartitionMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL, table,
        getPartitionKeyValues(table, partitionsIterator), now());
  }

  public CreateFunctionMessage buildCreateFunctionMessage(Function fn) {
    return new JSONCreateFunctionMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL, fn, now());
  }

  public DropFunctionMessage buildDropFunctionMessage(Function fn) {
    return new JSONDropFunctionMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL, fn, now());
  }

  public CreateIndexMessage buildCreateIndexMessage(Index idx) {
    return new JSONCreateIndexMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL, idx, now());
  }

  public DropIndexMessage buildDropIndexMessage(Index idx) {
    return new JSONDropIndexMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL, idx, now());
  }

  public AlterIndexMessage buildAlterIndexMessage(Index before, Index after) {
    return new JSONAlterIndexMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL, before, after, now());
  }

  public InsertMessage buildInsertMessage(Table tableObj, Partition partObj,
      boolean replace, Iterator<String> fileIter) {
    return new JSONInsertMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL,
        tableObj, partObj, replace, fileIter, now());
  }


  public JSONUpdateTableColumnStatMessage buildUpdateTableColumnStatMessage(ColumnStatistics colStats,
                                                                        Table tableObj) {
    return new JSONUpdateTableColumnStatMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL, now(),
            colStats, tableObj);
  }


  public JSONUpdatePartitionColumnStatMessage buildUpdatePartitionColumnStatMessage(ColumnStatistics colStats,
                                                                                List<String> partVals,
                                                                                Table tableObj) {
    return new JSONUpdatePartitionColumnStatMessage(MS_SERVER_URL, MS_SERVICE_PRINCIPAL, now(), colStats,
            partVals,tableObj);
  }

  private long now() {
    return System.currentTimeMillis() / 1000;
  }

  public static String createDatabaseObjJson(Database dbObj) throws TException {
    TSerializer serializer = new TSerializer(new TJSONProtocol.Factory());
    return serializer.toString(dbObj, "UTF-8");
  }

  public static String createTableObjJson(Table tableObj) throws TException {
    TSerializer serializer = new TSerializer(new TJSONProtocol.Factory());
    return serializer.toString(tableObj, "UTF-8");
  }

  public static String createPartitionObjJson(Partition partitionObj) throws TException {
    TSerializer serializer = new TSerializer(new TJSONProtocol.Factory());
    return serializer.toString(partitionObj, "UTF-8");
  }

  public static String createFunctionObjJson(Function functionObj) throws TException {
    TSerializer serializer = new TSerializer(new TJSONProtocol.Factory());
    return serializer.toString(functionObj, "UTF-8");
  }

  public static String createIndexObjJson(Index indexObj) throws TException {
    TSerializer serializer = new TSerializer(new TJSONProtocol.Factory());
    return serializer.toString(indexObj, "UTF-8");
  }

  public static String createTableColumnStatJson(ColumnStatistics colStats) throws TException {
    TSerializer serializer = new TSerializer(new TJSONProtocol.Factory());
    return serializer.toString(colStats, "UTF-8");
  }

  public static Table getTableObj(ObjectNode jsonTree) throws Exception {
    TDeserializer deSerializer = new TDeserializer(new TJSONProtocol.Factory());
    Table tableObj = new Table();
    String tableJson = jsonTree.get("tableObjJson").asText();
    deSerializer.deserialize(tableObj, tableJson, "UTF-8");
    return tableObj;
  }

  /*
   * TODO: Some thoughts here : We have a current todo to move some of these methods over to
   * MessageFactory instead of being here, so we can override them, but before we move them over,
   * we should keep the following in mind:
   *
   * a) We should return Iterables, not Lists. That makes sure that we can be memory-safe when
   * implementing it rather than forcing ourselves down a path wherein returning List is part of
   * our interface, and then people use .size() or somesuch which makes us need to materialize
   * the entire list and not change. Also, returning Iterables allows us to do things like
   * Iterables.transform for some of these.
   * b) We should not have "magic" names like "tableObjJson", because that breaks expectation of a
   * couple of things - firstly, that of serialization format, although that is fine for this
   * JSONMessageEncoder, and secondly, that makes us just have a number of mappings, one for each
   * obj type, and sometimes, as the case is with alter, have multiples. Also, any event-specific
   * item belongs in that event message / event itself, as opposed to in the factory. It's okay to
   * have utility accessor methods here that are used by each of the messages to provide accessors.
   * I'm adding a couple of those here.
   *
   */

  public static TBase getTObj(String tSerialized, Class<? extends TBase> objClass)
      throws Exception {
    TDeserializer thriftDeSerializer = new TDeserializer(new TJSONProtocol.Factory());
    TBase obj = objClass.newInstance();
    thriftDeSerializer.deserialize(obj, tSerialized, "UTF-8");
    return obj;
  }

  public static Iterable<? extends TBase> getTObjs(
      Iterable<String> objRefStrs, final Class<? extends TBase> objClass) throws Exception {

    try {
      return Iterables.transform(objRefStrs, new com.google.common.base.Function<String, TBase>() {

        public TBase apply(@Nullable String objStr) {
          try {
            return getTObj(objStr, objClass);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }
      });
    } catch (RuntimeException re) {
      // We have to add this bit of exception handling here, because Function.apply does not allow us to throw
      // the actual exception that might be a checked exception, so we wind up needing to throw a RuntimeException
      // with the previously thrown exception as its cause. However, since RuntimeException.getCause() returns
      // a throwable instead of an Exception, we have to account for the possibility that the underlying code
      // might have thrown a Throwable that we wrapped instead, in which case, continuing to throw the
      // RuntimeException is the best thing we can do.
      Throwable t = re.getCause();
      if (t instanceof Exception) {
        throw (Exception) t;
      } else {
        throw re;
      }
    }
  }

  // If we do not need this format of accessor using ObjectNode, this is a candidate for removal as well
  public static Iterable<? extends TBase> getTObjs(
      ObjectNode jsonTree, String objRefListName, final Class<? extends TBase> objClass)
      throws Exception {
    Iterable<JsonNode> jsonArrayIterator = jsonTree.get(objRefListName);
    return getTObjs(Iterables.transform(jsonArrayIterator, JsonNode::asText), objClass);
  }

  public static Map<String, String> getPartitionKeyValues(Table table, Partition partition) {
    Map<String, String> partitionKeys = new LinkedHashMap<>();
    for (int i = 0; i < table.getPartitionKeysSize(); ++i) {
      partitionKeys.put(table.getPartitionKeys().get(i).getName(),
          partition.getValues().get(i));
    }
    return partitionKeys;
  }

  public static List<Map<String, String>> getPartitionKeyValues(final Table table,
      Iterator<Partition> iterator) {
    return Lists.newArrayList(Iterators
        .transform(iterator, partition -> getPartitionKeyValues(table, partition)));
  }
}
