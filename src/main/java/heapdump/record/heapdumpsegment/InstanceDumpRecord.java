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
    return new InstanceDumpRecord(
        objectId, stackTraceSerialNumber, classObjectId, bytes, reader.getIdentifierSize());
  }

  private final int identifierSize;
  private final long objectId;
  private final int stackTraceSerialNumber;
  private final long classObjectId;
  private final byte[] bytes;

  private InstanceDumpRecord(
      long objectId,
      int stackTraceSerialNumber,
      long classObjectId,
      byte[] bytes,
      int identifierSize) {
    this.objectId = objectId;
    this.stackTraceSerialNumber = stackTraceSerialNumber;
    this.classObjectId = classObjectId;
    this.bytes = bytes;
    this.identifierSize = identifierSize;
  }

  public long getObjectId() {
    return objectId;
  }

  public long getClassObjectId() {
    return classObjectId;
  }

  public FieldReader getFieldReader() {
    return new FieldReader();
  }

  public class FieldReader {
    private int index = 0;

    public long readField(BasicType type) {
      switch (type) {
        case OBJECT:
          switch (identifierSize) {
            case 1:
              return readField(BasicType.BYTE);
            case 2:
              return readField(BasicType.SHORT);
            case 4:
              return readField(BasicType.INT);
            case 8:
              return readField(BasicType.LONG);
            default:
              throw new IllegalArgumentException("Unknown ID length " + identifierSize);
          }
        case BOOLEAN:
        case BYTE:
          return bytes[index++];
        case SHORT:
        case CHAR:
          short shortRet = 0;
          for (int i = 0; i < 2; i++) {
            shortRet <<= 8;
            short next = bytes[index++];
            shortRet |= (next & 0xff);
          }
          return shortRet;
        case FLOAT:
        case INT:
          int intRet = 0;
          for (int i = 0; i < 4; i++) {
            intRet <<= 8;
            int next = bytes[index++];
            intRet |= (next & 0xff);
          }
          return intRet;
        case DOUBLE:
        case LONG:
          long longRet = 0;
          for (int i = 0; i < 8; i++) {
            longRet <<= 8;
            long next = bytes[index++];
            longRet |= (next & 0xff);
          }
          return longRet;
        default:
          throw new IllegalArgumentException("Unknown type " + type);
      }
    }
  }
}
