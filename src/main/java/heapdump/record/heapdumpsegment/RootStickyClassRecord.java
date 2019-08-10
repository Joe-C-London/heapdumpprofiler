package heapdump.record.heapdumpsegment;

import heapdump.FileReader;
import java.io.IOException;

public class RootStickyClassRecord implements SubRecord {

  public static RootStickyClassRecord readRecord(FileReader reader) throws IOException {
    long objectId = reader.readId();
    return new RootStickyClassRecord(objectId);
  }

  private final long objectId;

  private RootStickyClassRecord(long objectId) {
    this.objectId = objectId;
  }
}
