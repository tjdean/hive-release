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
package org.apache.hive.hcatalog.pig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.pig.data.Tuple;
import org.junit.Test;

public class TestOrcHCatLoaderComplexSchema extends TestHCatLoaderComplexSchema {

  @Override
  protected String storageFormat() {
    return "orc";
  }
  /**
   * artificially complex nested schema to test nested schema conversion
   * @throws Exception
   */
  @Test
  public void testMapNullKey() throws Exception {
    String pigSchema = "m:map[]";

    String tableSchema = "m map<string, string>";

    List<Tuple> data = new ArrayList<Tuple>();
    Tuple t = t(
      new HashMap<String, String>() {
      {
        put("ac test1", "test 1");
        put("ac test2", "test 2");
        put(null, "test 3");
      };
    });
    data.add(t);
    verifyWriteRead("testSyntheticComplexSchema", pigSchema, tableSchema, data, true);
    verifyWriteRead("testSyntheticComplexSchema", pigSchema, tableSchema, data, false);
  }
}
