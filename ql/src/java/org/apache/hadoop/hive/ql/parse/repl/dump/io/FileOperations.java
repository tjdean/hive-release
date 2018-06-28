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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hive.ql.parse.repl.dump.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.common.FileUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.ReplChangeManager;
import org.apache.hadoop.hive.shims.Utils;
import org.apache.hadoop.hive.ql.parse.EximUtil;
import org.apache.hadoop.hive.ql.parse.LoadSemanticAnalyzer;
import org.apache.hadoop.hive.ql.parse.ReplicationSpec;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.hadoop.hive.ql.parse.repl.CopyUtils;
import org.apache.hadoop.hive.ql.ErrorMsg;

import javax.security.auth.login.LoginException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import static org.apache.hadoop.hive.ql.ErrorMsg.FILE_NOT_FOUND;

public class FileOperations {
  private static Log logger = LogFactory.getLog(FileOperations.class);
  private final Path dataFileListPath;
  private final Path exportRootDataDir;
  private final String distCpDoAsUser;
  private final HiveConf hiveConf;
  private FileSystem exportFileSystem;
  private FileSystem dataFileSystem;
  public static final int MAX_IO_ERROR_RETRY = 5;

  public FileOperations(Path dataFileListPath, Path exportRootDataDir,
                        String distCpDoAsUser, HiveConf hiveConf) throws IOException {
    this.dataFileListPath = dataFileListPath;
    this.exportRootDataDir = exportRootDataDir;
    this.distCpDoAsUser = distCpDoAsUser;
    this.hiveConf = hiveConf;
    dataFileSystem = dataFileListPath.getFileSystem(hiveConf);
    exportFileSystem = exportRootDataDir.getFileSystem(hiveConf);
  }

  public void export(ReplicationSpec forReplicationSpec) throws Exception {
    if (forReplicationSpec.isLazy()) {
      exportFilesAsList();
    } else {
      copyFiles();
    }
  }

  /**
   * This writes the actual data in the exportRootDataDir from the source.
   */
  private void copyFiles() throws IOException, LoginException {
    FileStatus[] fileStatuses = LoadSemanticAnalyzer.matchFilesOrDir(dataFileSystem, dataFileListPath);
    List<Path> srcPaths = new ArrayList<>();
    for (FileStatus fileStatus : fileStatuses) {
      srcPaths.add(fileStatus.getPath());
    }
    new CopyUtils(distCpDoAsUser, hiveConf).doCopy(exportRootDataDir, srcPaths);
  }

  private void writeFilesList(FileStatus[] fileStatuses, BufferedWriter writer)
          throws IOException {
    for (FileStatus fileStatus : fileStatuses) {
      if (fileStatus.isDirectory()) {
        // Write files inside the sub-directory.
        Path subDir = fileStatus.getPath();
        writeFilesList(listFilesInDir(subDir), writer);
      } else {
        writer.write(encodedUri(fileStatus));
        writer.newLine();
      }
    }
  }

  private FileStatus[] listFilesInDir(Path path) throws IOException {
    return dataFileSystem.listStatus(path, p -> {
      String name = p.getName();
      return !name.startsWith("_") && !name.startsWith(".");
    });
  }

  /**
   * This needs the root data directory to which the data needs to be exported to.
   * The data export here is a list of files either in table/partition that are written to the _files
   * in the exportRootDataDir provided.
   */
  private void exportFilesAsList() throws SemanticException, IOException, LoginException {
    boolean done = false;
    int repeat = 0;
    while (!done) {
      // This is only called for replication that handles MM tables; no need for mmCtx.
      try (BufferedWriter writer = writer()) {
        writeFilesList(listFilesInDir(dataFileListPath), writer);
        done = true;
      } catch (IOException e) {
        if (e instanceof FileNotFoundException) {
          logger.error("exporting data files in dir : " + dataFileListPath + " to " + exportRootDataDir + " failed");
          throw new FileNotFoundException(FILE_NOT_FOUND.format(e.getMessage()));
        }
        repeat++;
        logger.info("writeFilesList failed", e);
        if (repeat >= FileUtils.MAX_IO_ERROR_RETRY) {
          logger.error("exporting data files in dir : " + dataFileListPath + " to " + exportRootDataDir + " failed");
          throw new IOException(ErrorMsg.REPL_FILE_SYSTEM_OPERATION_RETRY.getMsg());
        }

        int sleepTime = FileUtils.getSleepTime(repeat - 1);
        logger.info(" sleep for " + sleepTime + " milliseconds for retry num " + repeat);
        try {
          Thread.sleep(sleepTime);
        } catch (InterruptedException timerEx) {
          logger.info("thread sleep interrupted", timerEx);
        }

        // in case of io error, reset the file system object
        FileSystem.closeAllForUGI(Utils.getUGI());
        dataFileSystem = dataFileListPath.getFileSystem(hiveConf);
        exportFileSystem = exportRootDataDir.getFileSystem(hiveConf);
        Path exportPath = new Path(exportRootDataDir, EximUtil.FILES_NAME);
        if (exportFileSystem.exists(exportPath)) {
          exportFileSystem.delete(exportPath, true);
        }
      }
    }
  }

  private BufferedWriter writer() throws IOException {
    Path exportToFile = new Path(exportRootDataDir, EximUtil.FILES_NAME);
    if (exportFileSystem.exists(exportToFile)) {
      throw new IllegalArgumentException(
          exportToFile.toString() + " already exists and cant export data from path(dir) "
              + dataFileListPath);
    }
    logger.debug("exporting data files in dir : " + dataFileListPath + " to " + exportToFile);
    return new BufferedWriter(
        new OutputStreamWriter(exportFileSystem.create(exportToFile))
    );
  }

  private String encodedUri(FileStatus fileStatus) throws IOException {
    Path currentDataFilePath = fileStatus.getPath();
    String checkSum = ReplChangeManager.checksumFor(currentDataFilePath, dataFileSystem);
    return ReplChangeManager.encodeFileUri(currentDataFilePath.toString(), checkSum);
  }
}
