package heapdump.record;

import heapdump.FileReader;
import java.io.IOException;

public class StackTraceRecord implements Record {

  public static StackTraceRecord readRecord(FileReader reader) throws IOException {
    int stackTraceSerialNumber = reader.readInt();
    int threadSerialNumber = reader.readInt();
    long[] stackFrameIds = new long[reader.readInt()];
    for (int i = 0; i < stackFrameIds.length; i++) {
      stackFrameIds[i] = reader.readId();
    }
    return new StackTraceRecord(stackTraceSerialNumber, threadSerialNumber, stackFrameIds);
  }

  private final int stackTraceSerialNumber;
  private final int threadSerialNumber;
  private final long[] stackFrameIds;

  private StackTraceRecord(
      int stackTraceSerialNumber, int threadSerialNumber, long[] stackFrameIds) {
    this.stackTraceSerialNumber = stackTraceSerialNumber;
    this.threadSerialNumber = threadSerialNumber;
    this.stackFrameIds = stackFrameIds;
  }
}
