/*
 * Copyright 2015 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.ql.io.orc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.common.DiskRangeList;
import org.apache.hadoop.hive.common.DiskRangeList.DiskRangeListCreateHelper;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.shims.HadoopShims.ZeroCopyReaderShim;
import org.apache.hadoop.io.Text;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestOrcLargeStripe {

  private static final long MB = 1024 * 1024;
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

  @Mock
  private FSDataInputStream mockDataInput;

  private DiskRangeList createRangeList(long... stripeSizes) {
    DiskRangeListCreateHelper list = new DiskRangeListCreateHelper();
    long prev = 0;
    for (long stripe : stripeSizes) {
      list.addOrMerge(prev, stripe, true, true);
      prev = stripe;
    }
    return list.extract();
  }

  private void verifyDiskRanges(long stripeLength, int expectedChunks) throws Exception {

    DiskRangeList rangeList = createRangeList(stripeLength);

    DiskRangeList newList = RecordReaderUtils.readDiskRanges(mockDataInput, null, 0, rangeList, false, (int) (2 * MB));
    assertEquals(expectedChunks, newList.listSize());

    newList = RecordReaderUtils.readDiskRanges(mockDataInput, null, 0, rangeList, true, (int) (2 * MB));
    assertEquals(expectedChunks, newList.listSize());

    ZeroCopyReaderShim mockZcr = mock(ZeroCopyReaderShim.class);
    when(mockZcr.readBuffer(anyInt(), anyBoolean())).thenReturn(ByteBuffer.allocate((int) (2 * MB)));
    newList = RecordReaderUtils.readDiskRanges(mockDataInput, mockZcr, 0, rangeList, true, (int) (2 * MB));
    assertEquals(expectedChunks, newList.listSize());
  }

  @Test
  public void testStripeSizesBelowAndGreaterThanLimit() throws Exception {
    verifyDiskRanges(MB, 1);
    verifyDiskRanges(5 * MB, 3);
  }


  @Test
  public void testConfigMaxChunkLimit() throws IOException {
    Configuration conf = new Configuration();
    FileSystem fs = FileSystem.getLocal(conf);
    ObjectInspector inspector;
    synchronized (TestOrcFile.class) {
      inspector = ObjectInspectorFactory.getReflectionObjectInspector(Timestamp.class,
        ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
    }
    testFilePath = new Path(workDir, "TestOrcLargeStripe." +
      testCaseName.getMethodName() + ".orc");
    Writer writer = OrcFile.createWriter(testFilePath,
      OrcFile.writerOptions(conf).inspector(inspector).stripeSize(100000).bufferSize(10000)
        .version(OrcFile.Version.V_0_11).fileSystem(fs));
    writer.close();

    try {
      OrcFile.ReaderOptions opts = OrcFile.readerOptions(conf);
      Reader reader = OrcFile.createReader(testFilePath, opts);
      RecordReader recordReader = reader.rowsOptions(new Reader.Options().range(0L, Long.MAX_VALUE));
      assertTrue(recordReader instanceof RecordReaderImpl);
      assertEquals(Integer.MAX_VALUE - 1024, ((RecordReaderImpl) recordReader).getMaxDiskRangeChunkLimit());

      conf = new Configuration();
      conf.setInt(HiveConf.ConfVars.HIVE_ORC_MAX_DISK_RANGE_CHUNK_LIMIT.varname, 1000);
      opts = OrcFile.readerOptions(conf);
      reader = OrcFile.createReader(testFilePath, opts);
      recordReader = reader.rowsOptions(new Reader.Options().range(0L, Long.MAX_VALUE));
      assertTrue(recordReader instanceof RecordReaderImpl);
      assertEquals(1000, ((RecordReaderImpl) recordReader).getMaxDiskRangeChunkLimit());
    } finally {
      fs.delete(testFilePath, false);
    }
  }

  // Comment this test if the test infra cannot afford 4GB heap/or if the disks are horribly slow
  @Test
  public void testStringDirectGreaterThan2GB() throws IOException {
    ObjectInspector inspector;
    synchronized (TestOrcFile.class) {
      inspector = ObjectInspectorFactory.getReflectionObjectInspector(Text.class,
        ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
    }
    try {
      conf.setDouble("hive.exec.orc.dictionary.key.size.threshold", 0.0);
      Writer writer = OrcFile.createWriter(
        testFilePath,
        OrcFile.writerOptions(conf).inspector(inspector)
          .compress(CompressionKind.NONE)
          .bufferSize(10000));
      int size = 5000;
      int width = 500_000;
      int[] input = new int[size];
      for (int i = 0; i < size; i++) {
        input[i] = width;
      }
      Random random = new Random(123);
      byte[] randBytes = new byte[width];
      for (final int ignored : input) {
        random.nextBytes(randBytes);
        writer.addRow(new Text(randBytes));
      }
      writer.close();

      Reader reader = OrcFile.createReader(testFilePath, OrcFile.readerOptions(conf).filesystem(fs));
      RecordReader rows = reader.rows();
      int rowsRead = 0;
      while (rows.hasNext()) {
        rows.next(null);
        rowsRead++;
      }
      assertEquals(size, rowsRead);
    } finally {
      fs.delete(testFilePath, false);
    }
  }
}