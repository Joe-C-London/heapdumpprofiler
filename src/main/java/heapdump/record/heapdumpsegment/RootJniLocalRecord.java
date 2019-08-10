package heapdump.record.heapdumpsegment;

import heapdump.FileReader;
import java.io.IOException;

public class RootJniLocalRecord implements SubRecord {

  public static RootJniLocalRecord readRecord(FileReader reader) throws IOException {
    long objectId = reader.readId();
    int threadSerialNumber = reader.readInt();
    int frameNumberInStacktrace = reader.readInt();
    return new RootJniLocalRecord(objectId, threadSerialNumber, frameNumberInStacktrace);
  }

  private final long objectId;
  private final int threadSerialNumber;
  private final int frameNumberInStacktrace;

  private RootJniLocalRecord(long objectId, int threadSerialNumber, int frameNumberInStacktrace) {
    this.objectId = objectId;
    this.threadSerialNumber = threadSerialNumber;
    this.frameNumberInStacktrace = frameNumberInStacktrace;
  }
}
