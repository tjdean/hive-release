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

package org.apache.hadoop.hive.ql.exec.vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator;
import org.apache.hadoop.hive.ql.exec.SMBMapJoinOperator;
import org.apache.hadoop.hive.ql.exec.vector.expressions.VectorExpression;
import org.apache.hadoop.hive.ql.exec.vector.expressions.VectorExpressionWriter;
import org.apache.hadoop.hive.ql.exec.vector.expressions.VectorExpressionWriterFactory;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.plan.ExprNodeDesc;
import org.apache.hadoop.hive.ql.plan.OperatorDesc;
import org.apache.hadoop.hive.ql.plan.SMBJoinDesc;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;

/**
 * VectorSMBJoinOperator.
 * Implements the vectorized pass-through SMB join operator. The implementation relies on the row-mode SMB join operator.
 * It accepts a vectorized batch input from the big table and iterates over the batch, calling the parent row-mode
 * implementation for each row in the batch.
 */
public class VectorSMBMapJoinOuterFilteredOperator extends SMBMapJoinOperator implements VectorizationContextRegion {

  private static final Log LOG = LogFactory.getLog(
      VectorSMBMapJoinOuterFilteredOperator.class.getName());  
  
  private static final long serialVersionUID = 1L;

  private VectorExpression[] bigTableFilterExpressions;

  private VectorizationContext vOutContext;

  // The above members are initialized by the constructor and must not be
  // transient.
  //---------------------------------------------------------------------------

  private transient VectorExpressionWriter[] rowWriters;

  private transient Object[] singleRow ;

  private transient VectorizedRowBatch outputBatch;

  private transient Map<ObjectInspector, VectorColumnAssign[]> outputVectorAssigners;

  private transient VectorizedRowBatchCtx vrbCtx = null;

  public VectorSMBMapJoinOuterFilteredOperator() {
    super();
  }
  
  public VectorSMBMapJoinOuterFilteredOperator(VectorizationContext vContext, OperatorDesc conf)
      throws HiveException {
    this();
    SMBJoinDesc desc = (SMBJoinDesc) conf;
    this.conf = desc;
    

    if (desc.isNoOuterJoin()) {
      // It is only valid to pre-filter for INNER JOIN.  OUTER JOIN requires post-ON condition
      // evaluation of filters.  They will be done by our super class SMBMapJoinOperator.
      Map<Byte, List<ExprNodeDesc>> filterExpressions = desc.getFilters();
      bigTableFilterExpressions = vContext.getVectorExpressions(filterExpressions.get((byte) desc.getPosBigTable()),
              VectorExpressionDescriptor.Mode.FILTER);
    }

    // We are making a new output vectorized row batch.
    vOutContext = new VectorizationContext(desc.getOutputColumnNames());
    vOutContext.setFileKey(vContext.getFileKey() + "/SMB_JOIN_" + desc.getBigTableAlias());
  }

  @Override
  protected void initializeOp(Configuration hconf) throws HiveException {
    // Use a final variable to parameterize the processVectorInspector closure.
    final int posBigTable = conf.getPosBigTable();

    // We need a input object inspector that is for the row we will extract out of the
    // big table vectorized row batch, not for example, an original inspector for an
    // ORC table, etc.
    VectorExpressionWriterFactory.processVectorInspector(
        (StructObjectInspector) inputObjInspectors[posBigTable],
        new VectorExpressionWriterFactory.SingleOIDClosure() {
          @Override
          public void assign(VectorExpressionWriter[] writers,
                  ObjectInspector objectInspector) {
             rowWriters = writers;
             inputObjInspectors[posBigTable] = objectInspector;
          }
        }
    );

    singleRow = new Object[rowWriters.length];

    // Call VectorSMBMapJoinOuterFilteredOperator with new input inspector.
    super.initializeOp(hconf);

    vrbCtx = new VectorizedRowBatchCtx();
    vrbCtx.init(vOutContext.getScratchColumnTypeMap(), (StructObjectInspector) this.outputObjInspector);

    outputBatch = vrbCtx.createVectorizedRowBatch();

    outputVectorAssigners = new HashMap<ObjectInspector, VectorColumnAssign[]>();
  }

  @Override
  public void processOp(Object row, int tag) throws HiveException {
    byte alias = (byte) tag;

    if (alias != this.posBigTable) {
      super.processOp(row, tag);
    } else {
  
      VectorizedRowBatch inBatch = (VectorizedRowBatch) row;
  
      if (conf.isNoOuterJoin() && bigTableFilterExpressions != null) {
        // It is only valid to pre-filter for INNER JOIN.  OUTER JOIN requires post-ON condition
        // evaluation of filters.  They will be done by our super class SMBMapJoinOperator.
        for(VectorExpression ve : bigTableFilterExpressions) {
          ve.evaluate(inBatch);
        }
      }
    
      for (int i = 0; i < inBatch.size; i++) {
        Object rowFromBatch = getRowObject(inBatch, i);
        super.processOp(rowFromBatch, tag);
      }
    }
  }
  
  @Override
  public void closeOp(boolean aborted) throws HiveException {
    // Make our super class finish before we flush our output batch.
    super.closeOp(aborted);

    if (!aborted && 0 < outputBatch.size) {
      flushOutput();
    }
  }
  
  @Override
  protected void internalForward(Object row, ObjectInspector outputOI) throws HiveException {
    Object[] values = (Object[]) row;
    VectorColumnAssign[] vcas = outputVectorAssigners.get(outputOI);
    if (null == vcas) {
      vcas = VectorColumnAssignFactory.buildAssigners(
          outputBatch, outputOI, vOutContext.getProjectionColumnMap(), conf.getOutputColumnNames());
      outputVectorAssigners.put(outputOI, vcas);
    }
    for (int i = 0; i < values.length; ++i) {
      vcas[i].assignObjectValue(values[i], outputBatch.size);
    }
    ++outputBatch.size;
    if (outputBatch.size == VectorizedRowBatch.DEFAULT_SIZE) {
      flushOutput();
    }
  }
  
  private void flushOutput() throws HiveException {
    forward(outputBatch, null);
    outputBatch.reset();
  }  

  private Object[] getRowObject(VectorizedRowBatch vrg, int rowIndex)
      throws HiveException {
    int batchIndex = rowIndex;
    if (vrg.selectedInUse) {
      batchIndex = vrg.selected[rowIndex];
    }

    for (int i = 0; i < vrg.projectionSize; i++) {
      ColumnVector vectorColumn = vrg.cols[vrg.projectedColumns[i]];
      if (vectorColumn != null) {
        int adjustedIndex = (vectorColumn.isRepeating ? 0 : batchIndex);
        singleRow[i] = rowWriters[i].writeValue(vectorColumn, adjustedIndex);
      } else {
        // Some columns from tables are not used.
        singleRow[i] = null;
      }
    }
    return singleRow;
  }

  @Override
  public VectorizationContext getOuputVectorizationContext() {
    return vOutContext;
  }
}
