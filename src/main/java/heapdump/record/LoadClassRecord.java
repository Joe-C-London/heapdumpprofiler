package heapdump.record;

import heapdump.FileReader;
import java.io.IOException;

public class LoadClassRecord implements Record {

  public static LoadClassRecord readRecord(FileReader reader) throws IOException {
    int classSerialNumber = reader.readInt();
    long classObjectId = reader.readId();
    int stackTraceSerialNumber = reader.readInt();
    long classNameStringId = reader.readId();
    return new LoadClassRecord(
        classSerialNumber, classObjectId, stackTraceSerialNumber, classNameStringId);
  }

  private final int classSerialNumber;
  private final long classObjectId;
  private final int stackTraceSerialNumber;
  private final long classNameStringId;

  private LoadClassRecord(
      int classSerialNumber,
      long classObjectId,
      int stackTraceSerialNumber,
      long classNameStringId) {
    this.classSerialNumber = classSerialNumber;
    this.classObjectId = classObjectId;
    this.stackTraceSerialNumber = stackTraceSerialNumber;
    this.classNameStringId = classNameStringId;
  }
}
