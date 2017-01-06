package org.apache.hive.jdbc.logs;

import org.apache.hive.service.cli.thrift.TProgressUpdateResp;

public interface InPlaceUpdateStream {
  void update(TProgressUpdateResp response);

  InPlaceUpdateStream NO_OP = new InPlaceUpdateStream() {
    @Override
    public void update(TProgressUpdateResp response) {

    }
  };
}
