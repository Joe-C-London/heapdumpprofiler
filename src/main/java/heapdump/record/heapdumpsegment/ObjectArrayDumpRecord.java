package heapdump.record.heapdumpsegment;

import heapdump.FileReader;
import java.io.IOException;

public class ObjectArrayDumpRecord implements SubRecord {

  public static ObjectArrayDumpRecord readRecord(FileReader reader) throws IOException {
    long arrayObjectId = reader.readId();
    int stackTraceSerialNumber = reader.readInt();
    long[] elements = new long[reader.readInt()];
    long arrayClassObjectId = reader.readId();
    for (int i = 0; i < elements.length; i++) {
      elements[i] = reader.readId();
    }
    return new ObjectArrayDumpRecord(
        arrayObjectId, stackTraceSerialNumber, arrayClassObjectId, elements);
  }

  private final long arrayObjectId;
  private final int stackTraceSerialNumber;
  private final long arrayClassObjectId;
  private final long[] elements;

  private ObjectArrayDumpRecord(
      long arrayObjectId, int stackTraceSerialNumber, long arrayClassObjectId, long[] elements) {
    this.arrayObjectId = arrayObjectId;
    this.stackTraceSerialNumber = stackTraceSerialNumber;
    this.arrayClassObjectId = arrayClassObjectId;
    this.elements = elements;
  }

  public long getArrayObjectId() {
    return arrayObjectId;
  }

  public long getArrayClassObjectId() {
    return arrayClassObjectId;
  }

  public long[] getElements() {
    return elements;
  }
}
