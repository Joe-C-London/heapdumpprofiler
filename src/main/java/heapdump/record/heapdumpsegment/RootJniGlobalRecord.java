package heapdump.record.heapdumpsegment;

import heapdump.FileReader;
import java.io.IOException;

public class RootJniGlobalRecord implements SubRecord {

  public static RootJniGlobalRecord readRecord(FileReader reader) throws IOException {
    long objectId = reader.readId();
    long jniGlobalRefId = reader.readId();
    return new RootJniGlobalRecord(objectId, jniGlobalRefId);
  }

  private final long objectId;
  private final long jniGlobalRefId;

  private RootJniGlobalRecord(long objectId, long jniGlobalRefId) {
    this.objectId = objectId;
    this.jniGlobalRefId = jniGlobalRefId;
  }
}
