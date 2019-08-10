package heapdump.record.heapdumpsegment;

import heapdump.FileReader;
import java.io.IOException;

public class RootThreadBlockRecord implements SubRecord {

  public static RootThreadBlockRecord readRecord(FileReader reader) throws IOException {
    long objectId = reader.readId();
    int threadSerialNumber = reader.readInt();
    return new RootThreadBlockRecord(objectId, threadSerialNumber);
  }

  private final long objectId;
  private final int threadSerialNumber;

  private RootThreadBlockRecord(long objectId, int threadSerialNumber) {
    this.objectId = objectId;
    this.threadSerialNumber = threadSerialNumber;
  }
}
