package heapdump.record.heapdumpsegment;

import heapdump.FileReader;
import java.io.IOException;

public class RootMonitorUsedRecord implements SubRecord {

  public static RootMonitorUsedRecord readRecord(FileReader reader) throws IOException {
    long objectId = reader.readId();
    return new RootMonitorUsedRecord(objectId);
  }

  private final long objectId;

  private RootMonitorUsedRecord(long objectId) {
    this.objectId = objectId;
  }
}
