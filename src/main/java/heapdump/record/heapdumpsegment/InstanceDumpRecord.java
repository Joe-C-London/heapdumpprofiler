package heapdump.record.heapdumpsegment;

import heapdump.FileReader;
import java.io.IOException;

public class InstanceDumpRecord implements SubRecord {

  public static InstanceDumpRecord readRecord(FileReader reader) throws IOException {
    long objectId = reader.readId();
    int stackTraceSerialNumber = reader.readInt();
    long classObjectId = reader.readId();
    byte[] bytes = new byte[reader.readInt()];
    reader.read(bytes);
    return new InstanceDumpRecord(objectId, stackTraceSerialNumber, classObjectId, bytes);
  }

  private final long objectId;
  private final int stackTraceSerialNumber;
  private final long classObjectId;
  private final byte[] bytes;

  private InstanceDumpRecord(
      long objectId, int stackTraceSerialNumber, long classObjectId, byte[] bytes) {
    this.objectId = objectId;
    this.stackTraceSerialNumber = stackTraceSerialNumber;
    this.classObjectId = classObjectId;
    this.bytes = bytes;
  }
}
