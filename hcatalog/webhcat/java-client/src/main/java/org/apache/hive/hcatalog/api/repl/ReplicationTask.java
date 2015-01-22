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

import org.apache.hive.hcatalog.api.HCatNotificationEvent;
import org.apache.hive.hcatalog.common.HCatConstants;
import org.apache.hive.hcatalog.messaging.MessageFactory;

import java.util.List;
import java.util.Map;

/**
 * ReplicationTask captures the concept of what it'd take to replicate changes from
 * one warehouse to another given a notification event that captures what changed.
 */
public class ReplicationTask {
  protected HCatNotificationEvent event;
  protected StagingDirectoryProvider srcStagingDirProvider = null;
  protected StagingDirectoryProvider dstStagingDirProvider = null;
  protected Map<String,String> tableNameMapping = null;
  protected Map<String,String> dbNameMapping = null;

  protected static MessageFactory messageFactory = MessageFactory.getInstance();

  public static boolean injectDebugMode = false; // FIXME : remove debug mode

  public interface Factory {
    public ReplicationTask create(HCatNotificationEvent event);
  }

  /**
   * EXIMFactory is an export-import based factory, this is the default factory.
   */
  public static class EXIMFactory implements Factory {
    public ReplicationTask create(HCatNotificationEvent event){
      // TODO : Java 1.7+ support using String with switches, but IDEs don't all seem to know that.
      // If casing is fine for now. But we should eventually remove this. Also, I didn't want to
      // create another enum just for this.
      if (event.getEventType().equals(HCatConstants.HCAT_CREATE_DATABASE_EVENT)) {
        return new NoopReplicationTask(event);
      } else if (event.getEventType().equals(HCatConstants.HCAT_DROP_DATABASE_EVENT)) {
        return new NoopReplicationTask(event);
      } else if (event.getEventType().equals(HCatConstants.HCAT_CREATE_TABLE_EVENT)) {
        return new NoopReplicationTask(event);
      } else if (event.getEventType().equals(HCatConstants.HCAT_DROP_TABLE_EVENT)) {
        return new NoopReplicationTask(event);
      } else if (event.getEventType().equals(HCatConstants.HCAT_ADD_PARTITION_EVENT)) {
        return new AddPartitionReplicationTask(event);
      } else if (event.getEventType().equals(HCatConstants.HCAT_DROP_PARTITION_EVENT)) {
        return new DropPartitionReplicationTask(event);
      } else {
        throw new IllegalStateException("Unrecognized Event type, no replication task available");
      }
    }
  }

  /**
   * Dummy NoopFactory for testing, returns a NoopReplicationTask for all recognized events.
   * Warning : this will eventually go away or move to the test section - it's intended only
   * for integration testing purposes.
   */
  public static class NoopFactory implements Factory {
    @Override
    public ReplicationTask create(HCatNotificationEvent event) {
      // TODO : Java 1.7+ support using String with switches, but IDEs don't all seem to know that.
      // If casing is fine for now. But we should eventually remove this. Also, I didn't want to
      // create another enum just for this.
      if (event.getEventType().equals(HCatConstants.HCAT_CREATE_DATABASE_EVENT)) {
        return new NoopReplicationTask(event);
      } else if (event.getEventType().equals(HCatConstants.HCAT_DROP_DATABASE_EVENT)) {
        return new NoopReplicationTask(event);
      } else if (event.getEventType().equals(HCatConstants.HCAT_CREATE_TABLE_EVENT)) {
        return new NoopReplicationTask(event);
      } else if (event.getEventType().equals(HCatConstants.HCAT_DROP_TABLE_EVENT)) {
        return new NoopReplicationTask(event);
      } else if (event.getEventType().equals(HCatConstants.HCAT_ADD_PARTITION_EVENT)) {
        return new NoopReplicationTask(event);
      } else if (event.getEventType().equals(HCatConstants.HCAT_DROP_PARTITION_EVENT)) {
        return new NoopReplicationTask(event);
      } else {
        throw new IllegalStateException("Unrecognized Event type, no replication task available");
      }
    }
  }

  private static Factory factoryInstance = null;
  private static Factory getFactoryInstance() {
    if (factoryInstance == null){
      // Eventually, we'll have a bit here that looks at a config param to instantiate
      // the appropriate factory, with EXIMFactory being the default - that allows
      // others to implement their own ReplicationTask.Factory for other replication
      // implementations.
      if (injectDebugMode){
        factoryInstance = new EXIMFactory();
      } else {
        factoryInstance = new NoopFactory();
      }
    }
    return factoryInstance;
  }

  /**
   * Factory method to return appropriate subtype of ReplicationTask for given event
   * @param event HCatEventMessage returned by the notification subsystem
   * @return corresponding ReplicationTask
   */
  public static ReplicationTask create(HCatNotificationEvent event){
    if (event == null){
      throw new IllegalArgumentException("event should not be null");
    }
    return getFactoryInstance().create(event);
  }

  // Primary entry point is a factory method instead of ctor
  // to allow for future ctor mutabulity in design
  protected ReplicationTask(HCatNotificationEvent event) {
    this.event = event;
  }

  /**
   * Returns the event that this ReplicationTask is attempting to replicate
   * @return underlying event
   */
  public HCatNotificationEvent getEvent(){
    return this.event;
  }

  /**
   * Returns true if the replication task in question needs to create staging
   * directories to complete its operation. This will mean that you will need
   * to copy these directories over to the destination warehouse for each
   * source-destination warehouse pair.
   * If this is true, you will need to call .withSrcStagingDirProvider(...)
   * and .withDstStagingDirProvider(...) before this ReplicationTask is usable
   */
  public boolean needsStagingDirs(){
    return (this.event.getEventType().equals(HCatConstants.HCAT_ADD_PARTITION_EVENT));
  }

  /**
   * Returns true if this ReplicationTask is prepared with all info it needs, and is
   * ready to be used
   */
  public boolean isActionable(){
    if (! this.needsStagingDirs()) {
      return true;
    }
    if ((srcStagingDirProvider != null) && (dstStagingDirProvider != null)){
      return true;
    }
    return false;
  }

  /**
   * See {@link org.apache.hive.hcatalog.api.repl.StagingDirectoryProvider}
   * @param srcStagingDirProvider Staging Directory Provider for the source warehouse
   * @return this
   */
  public ReplicationTask withSrcStagingDirProvider(StagingDirectoryProvider srcStagingDirProvider){
    this.srcStagingDirProvider = srcStagingDirProvider;
    return this;
  }

  /**
   * See {@link org.apache.hive.hcatalog.api.repl.StagingDirectoryProvider}
   * @param dstStagingDirProvider Staging Directory Provider for the destination warehouse
   * @return this replication task
   */
  public ReplicationTask withDstStagingDirProvider(StagingDirectoryProvider dstStagingDirProvider){
    this.dstStagingDirProvider = dstStagingDirProvider;
    return this;
  }

  /**
   * Allows a user to specify a table name mapping, where the keys are the names of the table
   * in the source warehouse, and the values are the table names on the destination warehouse
   * @param tableNameMapping
   * @return this replication task
   */
  public ReplicationTask withTableNameMapping(Map<String,String> tableNameMapping){
    this.tableNameMapping = tableNameMapping;
    return this;
  }

  /**
   * Allows a user to specify a db name mapping, where the keys are the names of the db
   * in the source warehouse, and the values are the db names on the destination warehouse
   * @param dbNameMapping
   * @return this replication task
   */
  public ReplicationTask withDbNameMapping(Map<String,String> dbNameMapping){
    this.dbNameMapping = dbNameMapping;
    return this;
  }

  protected void verifyActionable() {
    if (!this.isActionable()){
      throw new IllegalStateException("actionable command on task called when ReplicationTask is still not actionable.");
    }
  }

  /**
   * Returns a Iterable<Command> to send to a hive driver on the source warehouse
   *
   * If you *need* a List<Command> instead, you can use guava's
   * ImmutableList.copyOf(iterable) or Lists.newArrayList(iterable) to
   * get the underlying list, but this defeats the purpose of making this
   * interface an Iterable rather than a List, since it is very likely
   * that the number of Commands returned here will cause your process
   * to run OOM.
   */
  public Iterable<? extends Command> getSrcWhCommands() {
    verifyActionable();
    return null;
  }

  /**
   * Returns a Iterable<Command> to send to a hive driver on the source warehouse
   *
   * If you *need* a List<Command> instead, you can use guava's
   * ImmutableList.copyOf(iterable) or Lists.newArrayList(iterable) to
   * get the underlying list, but this defeats the purpose of making this
   * interface an Iterable rather than a List, since it is very likely
   * that the number of Commands returned here will cause your process
   * to run OOM.
   */
  public Iterable<? extends Command> getDstWhCommands() {
    verifyActionable();
    return null;
  }


}

