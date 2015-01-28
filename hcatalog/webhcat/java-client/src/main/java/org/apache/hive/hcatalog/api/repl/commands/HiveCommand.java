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
package org.apache.hive.hcatalog.api.repl.commands;

import org.apache.hadoop.conf.Configuration;
import org.apache.hive.hcatalog.api.HCatClient;
import org.apache.hive.hcatalog.api.repl.Command;
import org.apache.hive.hcatalog.common.HCatException;

/**
 * Dummy marker class, to indicate that all those that extend this are
 * Commands whose get() method returns hive ql commands that can be
 * sent to HiveDriver.
 */
public abstract class HiveCommand implements Command {

  // WARNING : This call is still under design and should be considered highly
  // experimental - there are no guarantees being made with it, and if it turns
  // out that this call is not supportable, it will be removed.
  abstract void run(HCatClient client, Configuration conf) throws HCatException;
  abstract boolean isRunnableFromHCatClient(); // returns true if run() can be run for this Command
}
