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
package org.apache.hadoop.hive.ql.io.orc;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf.ConfVars;
import org.apache.hadoop.hive.ql.io.orc.OrcFile.Version;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.io.Text;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TestStringDictionary {

  Path workDir = new Path(System.getProperty("test.tmp.dir", "target" + File.separator + "test"
      + File.separator + "tmp"));

  Configuration conf;
  FileSystem fs;
  Path testFilePath;

  @Rule
  public TestName testCaseName = new TestName();

  @Before
  public void openFileSystem() throws Exception {
    conf = new Configuration();
    fs = FileSystem.getLocal(conf);
    testFilePath = new Path(workDir, "TestOrcFile." + testCaseName.getMethodName() + ".orc");
    fs.delete(testFilePath, false);
  }

  @Test
  public void testTooManyDistinct() throws Exception {
    ObjectInspector inspector;
    synchronized (TestOrcFile.class) {
      inspector = ObjectInspectorFactory.getReflectionObjectInspector(Text.class,
          ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
    }

    Writer writer = OrcFile.createWriter(
        testFilePath,
        OrcFile.writerOptions(conf).inspector(inspector).compress(CompressionKind.NONE)
            .bufferSize(10000));
    for (int i = 0; i < 20000; i++) {
      writer.addRow(new Text(String.valueOf(i)));
    }
    writer.close();

    Reader reader = OrcFile.createReader(testFilePath, OrcFile.readerOptions(conf).filesystem(fs));
    RecordReader rows = reader.rows();
    int idx = 0;
    while (rows.hasNext()) {
      Object row = rows.next(null);
      assertEquals(new Text(String.valueOf(idx++)), row);
    }

    // make sure the encoding type is correct
    for (StripeInformation stripe : reader.getStripes()) {
      // hacky but does the job, this casting will work as long this test resides
      // within the same package as ORC reader
      OrcProto.StripeFooter footer = ((RecordReaderImpl) rows).readStripeFooter(stripe);
      for (int i = 0; i < footer.getColumnsCount(); ++i) {
        OrcProto.ColumnEncoding encoding = footer.getColumns(i);
        assertEquals(OrcProto.ColumnEncoding.Kind.DIRECT_V2, encoding.getKind());
      }
    }
  }

  @Test
  public void testHalfDistinct() throws Exception {
    ObjectInspector inspector;
    synchronized (TestOrcFile.class) {
      inspector = ObjectInspectorFactory.getReflectionObjectInspector(Text.class,
          ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
    }

    Writer writer = OrcFile.createWriter(
        testFilePath,
        OrcFile.writerOptions(conf).inspector(inspector).compress(CompressionKind.NONE)
            .bufferSize(10000));
    Random rand = new Random(123);
    int[] input = new int[20000];
    for (int i = 0; i < 20000; i++) {
      input[i] = rand.nextInt(10000);
    }

    for (int i = 0; i < 20000; i++) {
      writer.addRow(new Text(String.valueOf(input[i])));
    }
    writer.close();

    Reader reader = OrcFile.createReader(testFilePath, OrcFile.readerOptions(conf).filesystem(fs));
    RecordReader rows = reader.rows();
    int idx = 0;
    while (rows.hasNext()) {
      Object row = rows.next(null);
      assertEquals(new Text(String.valueOf(input[idx++])), row);
    }

    // make sure the encoding type is correct
    for (StripeInformation stripe : reader.getStripes()) {
      // hacky but does the job, this casting will work as long this test resides
      // within the same package as ORC reader
      OrcProto.StripeFooter footer = ((RecordReaderImpl) rows).readStripeFooter(stripe);
      for (int i = 0; i < footer.getColumnsCount(); ++i) {
        OrcProto.ColumnEncoding encoding = footer.getColumns(i);
        assertEquals(OrcProto.ColumnEncoding.Kind.DICTIONARY_V2, encoding.getKind());
      }
    }
  }

  @Test
  public void testTooManyDistinctCheckDisabled() throws Exception {
    ObjectInspector inspector;
    synchronized (TestOrcFile.class) {
      inspector = ObjectInspectorFactory.getReflectionObjectInspector(Text.class,
          ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
    }

    conf.setBoolean(ConfVars.HIVE_ORC_ROW_INDEX_STRIDE_DICTIONARY_CHECK.varname, false);
    Writer writer = OrcFile.createWriter(
        testFilePath,
        OrcFile.writerOptions(conf).inspector(inspector).compress(CompressionKind.NONE)
            .bufferSize(10000));
    for (int i = 0; i < 20000; i++) {
      writer.addRow(new Text(String.valueOf(i)));
    }
    writer.close();

    Reader reader = OrcFile.createReader(testFilePath, OrcFile.readerOptions(conf).filesystem(fs));
    RecordReader rows = reader.rows();
    int idx = 0;
    while (rows.hasNext()) {
      Object row = rows.next(null);
      assertEquals(new Text(String.valueOf(idx++)), row);
    }

    // make sure the encoding type is correct
    for (StripeInformation stripe : reader.getStripes()) {
      // hacky but does the job, this casting will work as long this test resides
      // within the same package as ORC reader
      OrcProto.StripeFooter footer = ((RecordReaderImpl) rows).readStripeFooter(stripe);
      for (int i = 0; i < footer.getColumnsCount(); ++i) {
        OrcProto.ColumnEncoding encoding = footer.getColumns(i);
        assertEquals(OrcProto.ColumnEncoding.Kind.DIRECT_V2, encoding.getKind());
      }
    }
  }

  @Test
  public void testHalfDistinctCheckDisabled() throws Exception {
    ObjectInspector inspector;
    synchronized (TestOrcFile.class) {
      inspector = ObjectInspectorFactory.getReflectionObjectInspector(Text.class,
          ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
    }

    conf.setBoolean(ConfVars.HIVE_ORC_ROW_INDEX_STRIDE_DICTIONARY_CHECK.varname, false);
    Writer writer = OrcFile.createWriter(
        testFilePath,
        OrcFile.writerOptions(conf).inspector(inspector).compress(CompressionKind.NONE)
            .bufferSize(10000));
    Random rand = new Random(123);
    int[] input = new int[20000];
    for (int i = 0; i < 20000; i++) {
      input[i] = rand.nextInt(10000);
    }

    for (int i = 0; i < 20000; i++) {
      writer.addRow(new Text(String.valueOf(input[i])));
    }
    writer.close();

    Reader reader = OrcFile.createReader(testFilePath, OrcFile.readerOptions(conf).filesystem(fs));
    RecordReader rows = reader.rows();
    int idx = 0;
    while (rows.hasNext()) {
      Object row = rows.next(null);
      assertEquals(new Text(String.valueOf(input[idx++])), row);
    }

    // make sure the encoding type is correct
    for (StripeInformation stripe : reader.getStripes()) {
      // hacky but does the job, this casting will work as long this test resides
      // within the same package as ORC reader
      OrcProto.StripeFooter footer = ((RecordReaderImpl) rows).readStripeFooter(stripe);
      for (int i = 0; i < footer.getColumnsCount(); ++i) {
        OrcProto.ColumnEncoding encoding = footer.getColumns(i);
        assertEquals(OrcProto.ColumnEncoding.Kind.DICTIONARY_V2, encoding.getKind());
      }
    }
  }

  @Test
  public void testTooManyDistinctV11AlwaysDictionary() throws Exception {
    ObjectInspector inspector;
    synchronized (TestOrcFile.class) {
      inspector = ObjectInspectorFactory.getReflectionObjectInspector(Text.class,
          ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
    }

    Writer writer = OrcFile.createWriter(
        testFilePath,
        OrcFile.writerOptions(conf).inspector(inspector).compress(CompressionKind.NONE)
            .version(Version.V_0_11).bufferSize(10000));
    for (int i = 0; i < 20000; i++) {
      writer.addRow(new Text(String.valueOf(i)));
    }
    writer.close();

    Reader reader = OrcFile.createReader(testFilePath, OrcFile.readerOptions(conf).filesystem(fs));
    RecordReader rows = reader.rows();
    int idx = 0;
    while (rows.hasNext()) {
      Object row = rows.next(null);
      assertEquals(new Text(String.valueOf(idx++)), row);
    }

    // make sure the encoding type is correct
    for (StripeInformation stripe : reader.getStripes()) {
      // hacky but does the job, this casting will work as long this test resides
      // within the same package as ORC reader
      OrcProto.StripeFooter footer = ((RecordReaderImpl) rows).readStripeFooter(stripe);
      for (int i = 0; i < footer.getColumnsCount(); ++i) {
        OrcProto.ColumnEncoding encoding = footer.getColumns(i);
        assertEquals(OrcProto.ColumnEncoding.Kind.DICTIONARY, encoding.getKind());
      }
    }

  }

  public static class TextPair {
    Text shortString;
    Text longString;

    TextPair(String shortString, String longString) {
      this.shortString = new Text(shortString);
      this.longString = new Text(longString);
    }
  }

  /**
   * Test that dictionaries can be disabled, per column. In this test, we want to disable DICTIONARY_V2 for the
   * `longString` column (presumably for a low hit-ratio), while preserving DICTIONARY_V2 for `shortString`.
   * @throws Exception on unexpected failure
   */
  @Test
  public void testDisableDictionaryForSpecificColumn() throws Exception {
    final String SHORT_STRING_VALUE = "foo";
    final String  LONG_STRING_VALUE = "BAAAAAAAAR!!";

    ObjectInspector inspector;
    synchronized (TestOrcFile.class) {
      inspector = ObjectInspectorFactory.getReflectionObjectInspector(TextPair.class,
              ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
    }

    conf.set(ConfVars.HIVE_ORC_DIRECT_ENCODING_COLUMNS.varname, "longString");

    Writer writer = OrcFile.createWriter(
            testFilePath,
            OrcFile.writerOptions(conf).inspector(inspector)
                    .compress(CompressionKind.NONE)
                    .bufferSize(10000));

    for (int i = 0; i < 20000; i++) {
      writer.addRow(new TextPair(SHORT_STRING_VALUE, LONG_STRING_VALUE));
    }
    writer.close();

    Reader reader = OrcFile.createReader(testFilePath, OrcFile.readerOptions(conf).filesystem(fs));
    RecordReader rows = reader.rows();

    StructObjectInspector readerInspector = (StructObjectInspector) reader.getObjectInspector();
    StringObjectInspector shortStringInspector = (StringObjectInspector) readerInspector.
        getStructFieldRef("shortString").getFieldObjectInspector();
    StringObjectInspector longStringInspector = (StringObjectInspector) readerInspector.
            getStructFieldRef("longString").getFieldObjectInspector();
    List<? extends StructField> fields = readerInspector.getAllStructFieldRefs();

    while (rows.hasNext()) {
      Object row = rows.next(null);
      assertEquals(SHORT_STRING_VALUE, shortStringInspector.getPrimitiveJavaObject(readerInspector.
          getStructFieldData(row, fields.get(0))));
      assertEquals(LONG_STRING_VALUE, longStringInspector.getPrimitiveJavaObject(readerInspector.
          getStructFieldData(row, fields.get(1))));
    }

    // make sure the encoding type is correct
    for (StripeInformation stripe : reader.getStripes()) {
      // hacky but does the job, this casting will work as long this test resides
      // within the same package as ORC reader
      OrcProto.StripeFooter footer = ((RecordReaderImpl) rows).readStripeFooter(stripe);
      for (int i = 0; i < footer.getColumnsCount(); ++i) {
        assertEquals(
                "Expected 3 columns in the footer: One for the Orc Struct, and two for its members.",
                3, footer.getColumnsCount());
        assertEquals(
                "The ORC schema struct should be DIRECT encoded.",
                OrcProto.ColumnEncoding.Kind.DIRECT, footer.getColumns(0).getKind()
        );
        assertEquals(
                "The shortString column must be DICTIONARY_V2 encoded",
                OrcProto.ColumnEncoding.Kind.DICTIONARY_V2, footer.getColumns(1).getKind()
        );
        assertEquals(
                "The longString column must be DIRECT_V2 encoded",
                OrcProto.ColumnEncoding.Kind.DIRECT_V2, footer.getColumns(2).getKind()
        );
      }
    }
  }

}
