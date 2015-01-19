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

public class ExportCommand implements Command {
  private String exportLocation;
  private String dbName = null;
  private String tableName = null;
  private Map<String, String> ptnDesc = null;

  public ExportCommand(Map<String, String> ptnDesc, String dbName, String tableName, String exportLocation) {
    this.dbName = dbName;
    this.tableName = tableName;
    this.ptnDesc = ptnDesc;
    this.exportLocation = exportLocation;
  }

  @Override
  public List<String> get() {
    // EXPORT TABLE tablename [PARTITION (part_column="value"[, ...])]
    // TO 'export_target_path'
    StringBuilder sb = new StringBuilder();
    sb.append("EXPORT TABLE ");
    sb.append(dbName);
    sb.append(".");
    sb.append(tableName); // FIXME : Handle quoted tablenames, or this will bite you
    sb.append(ReplicationUtils.partitionDescriptor(ptnDesc));
    sb.append(" TO '");
    sb.append(exportLocation);
    sb.append('\'');
    return Arrays.asList(sb.toString()); // TODO : verify that Arrays.asList instantiates correctly
  }

  @Override
  public boolean isRetriable() {
    return true; // Export is trivially retriable (after clearing out the staging dir provided.)
    // TODO : There needs to be a better way to tell the user to clear out which
    // staging directory this used, and thus, which to clear out
  }

  @Override
  public boolean isUndoable() {
    return true; // Export is trivially undoable - in that nothing needs doing to undo it.
  }

  @Override
  public List<String> getUndo() {
    return new ArrayList<String>(); // Nothing to undo.
  }

  @Override
  public void write(DataOutput dataOutput) throws IOException {
    ReaderWriter.writeDatum(dataOutput, dbName);
    ReaderWriter.writeDatum(dataOutput, tableName);
    ReaderWriter.writeDatum(dataOutput, ptnDesc);
    ReaderWriter.writeDatum(dataOutput, exportLocation);
  }

  @Override
  public void readFields(DataInput dataInput) throws IOException {
    dbName = (String)ReaderWriter.readDatum(dataInput);
    tableName = (String)ReaderWriter.readDatum(dataInput);
    ptnDesc = (Map<String,String>)ReaderWriter.readDatum(dataInput);
    exportLocation = (String)ReaderWriter.readDatum(dataInput);
  }
}
