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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.hive.hcatalog.api.repl;

import org.apache.hive.hcatalog.data.ReaderWriter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ImportCommand implements Command {
  private String importLocation;
  private String dbName = null;
  private String tableName = null;
  private Map<String, String> ptnDesc = null;


  // FIXME : The current implementation does not allow importing to an "EXTERNAL" location
  // We should ideally take a location for EXTERNAL tables, and specify that for the import
  // statement as well.

  public ImportCommand(Map<String, String> ptnDesc, String dbName, String tableName, String importLocation) {
    this.dbName = dbName;
    this.tableName = tableName;
    this.ptnDesc = ptnDesc;
    this.importLocation = importLocation;
  }

  @Override
  public List<String> get() {
    // IMPORT [[EXTERNAL] TABLE new_or_original_tablename [PARTITION (part_column="value"[, ...])]]
    // FROM 'source_path'
    //    [LOCATION 'import_target_path']
    StringBuilder sb = new StringBuilder();
    sb.append("IMPORT TABLE ");
    sb.append(dbName);
    sb.append('.');
    sb.append(tableName); // FIXME : Handle quoted tablenames, or this will bite you
    sb.append(ReplicationUtils.partitionDescriptor(ptnDesc));
    sb.append(" FROM '");
    sb.append(importLocation);
    sb.append('\'');
    return Arrays.asList(sb.toString());
  }



  @Override
  public boolean isRetriable() {
    return false; // If import failed, for reasons other than connection issues, it's likely not retriable.
  }

  @Override
  public boolean isUndoable() {
    return true; // It should be undoable to allow retry by dropping said partition, if it exists.
  }

  @Override
  public List<String> getUndo() {
    // ALTER TABLE table_name DROP [IF EXISTS] PARTITION partition_spec, PARTITION partition_spec,...;
    StringBuilder sb = new StringBuilder();
    sb.append("ALTER TABLE ");
    sb.append(dbName);
    sb.append('.');
    sb.append(tableName);
    sb.append(" DROP IF EXISTS ");
    sb.append(ReplicationUtils.partitionDescriptor(ptnDesc));
    return Arrays.asList(sb.toString());
  }

  @Override
  public List<String> cleanupLocationsPerRetry() {
    return new ArrayList<String>();
  }

  @Override
  public List<String> cleanupLocationsAfterEvent() {
    return Arrays.asList(importLocation);
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    ReaderWriter.writeDatum(dataOutput, dbName);
    ReaderWriter.writeDatum(dataOutput, tableName);
    ReaderWriter.writeDatum(dataOutput, ptnDesc);
    ReaderWriter.writeDatum(dataOutput, importLocation);
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    dbName = (String)ReaderWriter.readDatum(dataInput);
    tableName = (String)ReaderWriter.readDatum(dataInput);
    ptnDesc = (Map<String,String>)ReaderWriter.readDatum(dataInput);
    importLocation = (String)ReaderWriter.readDatum(dataInput);
  }
}

