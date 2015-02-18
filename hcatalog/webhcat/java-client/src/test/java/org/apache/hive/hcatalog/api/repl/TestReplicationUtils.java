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

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hive.hcatalog.api.repl.commands.ExportCommand;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class TestReplicationUtils extends TestCase {

  private static Log LOG = LogFactory.getLog(TestReplicationUtils.class.getName());

  public static void testToStringWordCharsOnly(){
    String s = "abc123!@#$%^&*()-_=[]{}\\|`~,<.>/?;:'\"XYZ";
    String stripped = ReplicationUtils.toStringWordCharsOnly(s);
    assertFalse(stripped.matches(".*\\W.*"));
    assertEquals("abc123_XYZ", stripped);
  }


  private static Map<String,Command> getTestCommands(){
    Map<String,Command> data = new HashMap<String,Command>();
    Map<String, String> ptnDesc = new HashMap<String, String>();
    ptnDesc.put("akey","1");
    ptnDesc.put("b.key","2");

    data.put("simple.export", new ExportCommand("dbname","tablename",ptnDesc,"/tmp/exportlocn/123/",true,1231231231));

    return data;
  }

  public static void testCommandSerialization() {

    for (Map.Entry<String,Command> entry : getTestCommands().entrySet()){
      Command cmd = entry.getValue();
      String serializedCmd = null;
      try {
        serializedCmd = ReplicationUtils.serializeCommand(cmd);
      } catch (IOException e) {
        LOG.error("Serialization error",e);
        assertNull(e); // error out.
      }

      LOG.info(entry.getKey() +"=>" + serializedCmd);

      Command cmd2 = null;
      try {
        cmd2 = ReplicationUtils.deserializeCommand(serializedCmd);
      } catch (IOException e) {
        LOG.error("Serialization error",e);
        assertNull(e); // error out.
      }

      assertEquals(cmd.getClass(),cmd2.getClass());
      assertEquals(cmd.getEventId(), cmd2.getEventId());
      assertEquals(cmd.get(), cmd2.get());
      assertEquals(cmd.isUndoable(),cmd2.isUndoable());
      if (cmd.isUndoable()){
        assertEquals(cmd.getUndo(),cmd2.getUndo());
      }
      assertEquals(cmd.isRetriable(),cmd2.isRetriable());
    }

  }

}
