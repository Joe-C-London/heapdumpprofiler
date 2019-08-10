package heapdump.record.heapdumpsegment;

import heapdump.FileReader;
import java.io.IOException;

public class RootThreadObjectRecord implements SubRecord {

  public static RootThreadObjectRecord readRecord(FileReader reader) throws IOException {
    long threadObjectId = reader.readId();
    int threadSerialNumber = reader.readInt();
    int stackTraceSerialNumber = reader.readInt();
    return new RootThreadObjectRecord(threadObjectId, threadSerialNumber, stackTraceSerialNumber);
  }

  private final long threadObjectId;
  private final int threadSerialNumber;
  private final int stackTraceSerialNumber;

  private RootThreadObjectRecord(
      long threadObjectId, int threadSerialNumber, int stackTraceSerialNumber) {
    this.threadObjectId = threadObjectId;
    this.threadSerialNumber = threadSerialNumber;
    this.stackTraceSerialNumber = stackTraceSerialNumber;
  }
}
