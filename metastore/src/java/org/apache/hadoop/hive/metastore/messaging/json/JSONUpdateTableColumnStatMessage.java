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

package org.apache.hadoop.hive.metastore.messaging.json;

import org.codehaus.jackson.annotate.JsonProperty;
import org.apache.hadoop.hive.metastore.api.ColumnStatistics;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.messaging.UpdateTableColumnStatMessage;
import org.apache.thrift.TException;

/**
 * JSON implementation of JSONUpdateTableColumnStatMessage
 */
public class JSONUpdateTableColumnStatMessage extends UpdateTableColumnStatMessage {

  @JsonProperty
  Long timestamp;

  @JsonProperty
  String server, servicePrincipal, db;

  @JsonProperty
  String colStatsJson;

  @JsonProperty
  String tableObjJson;

  /**
   * Default constructor, needed for Jackson.
   */
  public JSONUpdateTableColumnStatMessage() {
  }

  public JSONUpdateTableColumnStatMessage(String server, String servicePrincipal, Long timestamp,
                      ColumnStatistics colStats, Table tableObj) {
    this.timestamp = timestamp;
    this.server = server;
    this.servicePrincipal = servicePrincipal;
    this.db = colStats.getStatsDesc().getDbName();
    try {
      this.colStatsJson = JSONMessageFactory.createTableColumnStatJson(colStats);
      this.tableObjJson = JSONMessageFactory.createTableObjJson(tableObj);
    } catch (TException e) {
      throw new IllegalArgumentException("Could not serialize JSONUpdateTableColumnStatMessage : ", e);
    }
  }

  @Override
  public Long getTimestamp() {
    return timestamp;
  }

  @Override
  public String getDB() {
    return db;
  }

  @Override
  public String getServicePrincipal() {
    return servicePrincipal;
  }

  @Override
  public String getServer() {
    return server;
  }

  @Override
  public ColumnStatistics getColumnStatistics() {
    try {
      return  (ColumnStatistics) JSONMessageFactory.getTObj(colStatsJson, ColumnStatistics.class);
    } catch (Exception e) {
      throw new RuntimeException("failed to get the ColumnStatistics object ", e);
    }
  }

  @Override
  public String getColStatsJson() { return colStatsJson; }

  @Override
  public String getTableObjJson() { return tableObjJson; }

  @Override
  public Table getTableObject() throws Exception {
    return (Table) JSONMessageFactory.getTObj(tableObjJson, Table.class);
  }

  @Override
  public String toString() {
    try {
      return JSONMessageDeserializer.mapper.writeValueAsString(this);
    } catch (Exception exception) {
      throw new IllegalArgumentException("Could not serialize: ", exception);
    }
  }
}

