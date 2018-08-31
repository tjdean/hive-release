package org.apache.hadoop.hive.ql.io.orc;

import com.google.protobuf.CodedInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Contains different utilities for use with Protobuf
 */
public class PBUtils {
  private static final Logger LOG = LoggerFactory.getLogger(InStream.class);
  private static final int PROTOBUF_MESSAGE_MAX_LIMIT = 1024 << 20; // 1GB

  private PBUtils() {
    // Static class should not be instantiated
  }

  /**
   * Creates coded input stream (used for protobuf message parsing) with higher
   * message size limit.
   *
   * @param inStream   the stream to wrap.
   * @return coded input stream
   */
  public static CodedInputStream createCodedInputStream(InputStream inStream) {
    LOG.info("Created a coded input stream with higher limit {}", PROTOBUF_MESSAGE_MAX_LIMIT);
    CodedInputStream codedInputStream = CodedInputStream.newInstance(inStream);
    codedInputStream.setSizeLimit(PROTOBUF_MESSAGE_MAX_LIMIT);
    return codedInputStream;
  }
}
