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

import org.apache.hive.hcatalog.api.HCatDatabase;
import org.apache.hive.hcatalog.api.HCatPartition;
import org.apache.hive.hcatalog.api.HCatTable;

import javax.annotation.Nullable;
import java.util.Map;

public class ReplicationUtils {

  /**
   * Gets the last known replication state of this db. This is
   * applicable only if it is the destination of a replication
   * and has had data replicated into it via imports previously.
   * Defaults to 0.
   */
  public static long getLastReplicationId(HCatDatabase db){
    Map<String, String> props = db.getProperties();
    if (props != null){
      if (props.containsKey("repl.last.id")){
        return Long.parseLong(props.get("repl.last.id"));
      }
    }
    return 0l; // default is to return earliest possible state.
  }


  /**
   * Gets the last known replication state of the provided table. This
   * is applicable only if it is the destination of a replication
   * and has had data replicated into it via imports previously.
   * Defaults to 0.
   */
  public long getLastReplicationId(HCatTable tbl) {
    Map<String, String> tblProps = tbl.getTblProps();
    if (tblProps != null){
      if (tblProps.containsKey("repl.last.id")){
        return Long.parseLong(tblProps.get("repl.last.id"));
      }
    }
    return 0l; // default is to return earliest possible state.
  }

  /**
   * Gets the last known replication state of the provided partition.
   * This is applicable only if it is the destination of a replication
   * and has had data replicated into it via imports previously.
   * If that is not available, but parent table is provided,
   * defaults to parent table's replication state. If that is also
   * unknown, defaults to 0.
   */
  public long getLastReplicationId(HCatPartition ptn, @Nullable HCatTable parentTable) {
    Map<String,String> parameters = ptn.getParameters();
    if (parameters != null){
      if (parameters.containsKey("repl.last.id")){
        return Long.parseLong(parameters.get("repl.last.id"));
      }
    }

    if (parentTable != null){
      return getLastReplicationId(parentTable);
    }
    return 0l; // default is to return earliest possible state.
  }

  private ReplicationUtils(){
    // dummy private constructor, since this class is a collection of static utility methods.
  }

}
