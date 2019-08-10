package heapdump.record.heapdumpsegment;

import static heapdump.record.heapdumpsegment.BasicType.readValue;
import static heapdump.record.heapdumpsegment.BasicType.toBasicType;

import heapdump.FileReader;
import java.io.IOException;

public class PrimitiveArrayDumpRecord implements SubRecord {

  public static PrimitiveArrayDumpRecord readRecord(FileReader reader) throws IOException {
    long arrayObjectId = reader.readId();
    int stackTraceSerialNumber = reader.readInt();
    long[] elements = new long[reader.readInt()];
    BasicType elementType = toBasicType(reader.read());
    for (int i = 0; i < elements.length; i++) {
      elements[i] = readValue(reader, elementType);
    }
    return new PrimitiveArrayDumpRecord(
        arrayObjectId, stackTraceSerialNumber, elementType, elements);
  }

  private final long arrayObjectId;
  private final int stackTraceSerialNumber;
  private final BasicType elementType;
  private final long[] elements;

  private PrimitiveArrayDumpRecord(
      long arrayObjectId, int stackTraceSerialNumber, BasicType elementType, long[] elements) {
    this.arrayObjectId = arrayObjectId;
    this.stackTraceSerialNumber = stackTraceSerialNumber;
    this.elementType = elementType;
    this.elements = elements;
  }
}
