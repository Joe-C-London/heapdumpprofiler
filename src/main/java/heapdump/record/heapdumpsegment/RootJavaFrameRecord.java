package heapdump.record.heapdumpsegment;

import heapdump.FileReader;
import java.io.IOException;

public class RootJavaFrameRecord implements SubRecord {

  public static RootJavaFrameRecord readRecord(FileReader reader) throws IOException {
    long objectId = reader.readId();
    int threadSerialNumber = reader.readInt();
    int frameNumberInStackTrace = reader.readInt();
    return new RootJavaFrameRecord(objectId, threadSerialNumber, frameNumberInStackTrace);
  }

  private final long objectId;
  private final int threadSerialNumber;
  private final int frameNumberInStackTrace;

  private RootJavaFrameRecord(long objectId, int threadSerialNumber, int frameNumberInStackTrace) {
    this.objectId = objectId;
    this.threadSerialNumber = threadSerialNumber;
    this.frameNumberInStackTrace = frameNumberInStackTrace;
  }
}
