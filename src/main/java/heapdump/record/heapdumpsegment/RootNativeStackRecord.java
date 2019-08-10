package heapdump.record.heapdumpsegment;

import heapdump.FileReader;
import java.io.IOException;

public class RootNativeStackRecord implements SubRecord {

  public static RootNativeStackRecord readRecord(FileReader reader) throws IOException {
    long objectId = reader.readId();
    int threadSerialNumber = reader.readInt();
    return new RootNativeStackRecord(objectId, threadSerialNumber);
  }

  private final long objectId;
  private final int threadSerialNumber;

  private RootNativeStackRecord(long objectId, int threadSerialNumber) {
    this.objectId = objectId;
    this.threadSerialNumber = threadSerialNumber;
  }
}
