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

package org.apache.hadoop.hive.ql.udf.generic;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.WindowingSpec;
import org.apache.hadoop.hive.ql.parse.WindowingSpec.BoundarySpec;
import org.apache.hadoop.hive.ql.plan.ptf.BoundaryDef;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDAFEvaluator.AggregationBuffer;
import org.apache.hadoop.hive.ql.util.JavaDataModel;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

@SuppressWarnings({ "deprecation", "unchecked" })
public abstract class GenericUDAFStreamingEvaluator<T1> extends
    GenericUDAFEvaluator implements ISupportStreamingModeForWindowing {

  protected final GenericUDAFEvaluator wrappedEval;
  protected final WindowingSpec.Direction startDirection;
  protected final int startAmt;
  protected final int startRelativeOffset;
  protected final WindowingSpec.Direction endDirection;
  protected final int endAmt;
  protected final int endRelativeOffset;
  protected final int windowSize;

  public GenericUDAFStreamingEvaluator(GenericUDAFEvaluator wrappedEval,
      WindowingSpec.Direction startDirection, int startAmt,
      WindowingSpec.Direction endDirection, int endAmt) {
    this.wrappedEval = wrappedEval;
    this.startDirection = startDirection;
    this.startAmt = startAmt;
    this.endDirection = endDirection;
    this.endAmt = endAmt;
    this.mode = wrappedEval.mode;

    // Calculate window size
    if (startDirection == endDirection) {
      windowSize =  Math.abs(endAmt - startAmt) + 1;
    } else {
      windowSize =  startAmt + endAmt + 1;
    }

    // Calculate relative offset
    switch(startDirection) {
    case PRECEDING:
      startRelativeOffset = -startAmt;
      break;
    case FOLLOWING:
      startRelativeOffset = startAmt;
      break;
    default:
      startRelativeOffset = 0;
    }
    switch(endDirection) {
    case PRECEDING:
      endRelativeOffset = -endAmt;
      break;
    case FOLLOWING:
      endRelativeOffset = endAmt;
      break;
    default:
      endRelativeOffset = 0;
    } 
  }

  public int getWindowSize() {
    return windowSize;
  }

  class StreamingState extends AbstractAggregationBuffer {
    final AggregationBuffer wrappedBuf;
    protected final WindowingSpec.Direction startDirection;
    protected final int startAmt;
    protected final int startRelativeOffset;
    protected final WindowingSpec.Direction endDirection;
    protected final int endAmt;
    protected final int endRelativeOffset;
    protected final int windowSize;

    final List<T1> results;
    int numRows;

    StreamingState(
        WindowingSpec.Direction startDirection, int startAmt,
        WindowingSpec.Direction endDirection, int endAmt,
        AggregationBuffer buf) {
      this.wrappedBuf = buf;
      this.startDirection = startDirection;
      this.startAmt = startAmt;
      this.endDirection = endDirection;
      this.endAmt = endAmt;

      // Calculate window size
      if (startDirection == endDirection) {
        windowSize =  Math.abs(endAmt - startAmt) + 1;
      } else {
        windowSize =  startAmt + endAmt + 1;
      }

      // Calculate relative offset
      switch(startDirection) {
      case PRECEDING:
        startRelativeOffset = -startAmt;
        break;
      case FOLLOWING:
        startRelativeOffset = startAmt;
        break;
      default:
        startRelativeOffset = 0;
      }
      switch(endDirection) {
      case PRECEDING:
        endRelativeOffset = -endAmt;
        break;
      case FOLLOWING:
        endRelativeOffset = endAmt;
        break;
      default:
        endRelativeOffset = 0;
      }
      results = new ArrayList<T1>();
      numRows = 0;
    }

    protected void reset() {
      results.clear();
      numRows = 0;
    }

    /**
     * For the cases "X preceding and Y preceding" or the number of processed rows
     * is more than the size of FOLLOWING window, we are able to generate a PTF result
     * for a previous row.
     * @return
     */
    public boolean hasResultReady() {
      return this.numRows >= endRelativeOffset;
    }
  }

  @Override
  public ObjectInspector init(Mode m, ObjectInspector[] parameters)
      throws HiveException {
    return wrappedEval.init(m, parameters);
  }

  @Override
  public void reset(AggregationBuffer agg) throws HiveException {
    StreamingState ss = (StreamingState) agg;
    wrappedEval.reset(ss.wrappedBuf);
    ss.reset();
  }

  @Override
  public Object terminatePartial(AggregationBuffer agg) throws HiveException {
    throw new HiveException(getClass().getSimpleName()
        + ": terminatePartial not supported");
  }

  @Override
  public void merge(AggregationBuffer agg, Object partial) throws HiveException {
    throw new HiveException(getClass().getSimpleName()
        + ": merge not supported");
  }

  @Override
  public Object getNextResult(AggregationBuffer agg) throws HiveException {
    StreamingState ss = (StreamingState) agg;
    if (!ss.results.isEmpty()) {
      T1 res = ss.results.remove(0);
      if (res == null) {
        return ISupportStreamingModeForWindowing.NULL_RESULT;
      }
      return res;
    }
    return null;
  }

  public static abstract class SumAvgEnhancer<T1, T2> extends
      GenericUDAFStreamingEvaluator<T1> {

    public SumAvgEnhancer(GenericUDAFEvaluator wrappedEval, WindowingSpec.Direction startDirection, int startAmt,
          WindowingSpec.Direction endDirection, int endAmt) {
      super(wrappedEval, startDirection, startAmt,
             endDirection, endAmt);
    }

    class SumAvgStreamingState extends StreamingState {

      final List<T2> intermediateVals;  // Keep track of S[0..x]

      SumAvgStreamingState(WindowingSpec.Direction startDirection, int startAmt,
              WindowingSpec.Direction endDirection, int endAmt,
          AggregationBuffer buf) {
        super(startDirection, startAmt,
             endDirection, endAmt, buf);
        intermediateVals = new ArrayList<T2>();
      }

      @Override
      public int estimate() {
        if (!(wrappedBuf instanceof AbstractAggregationBuffer)) {
          return -1;
        }
        int underlying = ((AbstractAggregationBuffer) wrappedBuf).estimate();
        if (underlying == -1) {
          return -1;
        }
        if (startAmt == BoundarySpec.UNBOUNDED_AMOUNT) {
          return -1;
        }
        /*
         * sz Estimate = sz needed by underlying AggBuffer + sz for results + sz
         * for intermediates + 3 * JavaDataModel.PRIMITIVES1 sz of results = sz
         * of underlying * wdwSz sz of intermediates = sz of underlying * wdwSz
         */

        return underlying + (underlying * windowSize) + (underlying * windowSize)
            + (3 * JavaDataModel.PRIMITIVES1);
      }

      protected void reset() {
        intermediateVals.clear();
        super.reset();
      }

      /**
       * Retrieve the next stored intermediate result, i.e.,
       * Get S[x-1] in the computation of S[x..y] = S[y] - S[x-1].
       */
      public T2 retrieveNextIntermediateValue() {
        if (startAmt != BoundarySpec.UNBOUNDED_AMOUNT
            && !intermediateVals.isEmpty()
            && this.numRows >= windowSize) {
          return this.intermediateVals.remove(0);
        }

        return null;
      }
    }

    @Override
    public AggregationBuffer getNewAggregationBuffer() throws HiveException {
      AggregationBuffer underlying = wrappedEval.getNewAggregationBuffer();
      return new SumAvgStreamingState(startDirection, startAmt,
              endDirection, endAmt, underlying);
    }

    @Override
    public void iterate(AggregationBuffer agg, Object[] parameters)
        throws HiveException {
      SumAvgStreamingState ss = (SumAvgStreamingState) agg;

      wrappedEval.iterate(ss.wrappedBuf, parameters);

      // We need to insert 'null' before processing first row for the case: X preceding and y preceding
      if (ss.numRows == 0) {
        for (int i = ss.endRelativeOffset; i < 0; i++) {
          ss.results.add(null);
        }
      }

      // Generate the result for the windowing ending at the current row
      if (ss.hasResultReady()) {
        ss.results.add(getNextResult(ss));
      }
      if (ss.startAmt != BoundarySpec.UNBOUNDED_AMOUNT
          && ss.numRows + 1 >= ss.startRelativeOffset) {
        ss.intermediateVals.add(getCurrentIntermediateResult(ss));
      }

      ss.numRows++;
    }

    @Override
    public Object terminate(AggregationBuffer agg) throws HiveException {
      SumAvgStreamingState ss = (SumAvgStreamingState) agg;
      Object o = wrappedEval.terminate(ss.wrappedBuf);

      // After all the rows are processed, continue to generate results for the rows that results haven't generated.
      // For the case: X following and Y following, process first Y-X results and then insert X nulls.
      // For the case X preceding and Y following, process Y results.
      for (int i = Math.max(0, ss.startRelativeOffset); i < ss.endRelativeOffset; i++) {
        if (ss.hasResultReady()) {
          ss.results.add(getNextResult(ss));
        }
        ss.numRows++;
      }
      for (int i = 0; i < ss.startRelativeOffset; i++) {
        if (ss.hasResultReady()) {
          ss.results.add(null);
        }
        ss.numRows++;
      }
      return o;
    }

    @Override
    public int getRowsRemainingAfterTerminate() throws HiveException {
      throw new UnsupportedOperationException();
    }

    protected abstract T1 getNextResult(SumAvgStreamingState ss)
        throws HiveException;

    protected abstract T2 getCurrentIntermediateResult(SumAvgStreamingState ss)
        throws HiveException;

  }
}
