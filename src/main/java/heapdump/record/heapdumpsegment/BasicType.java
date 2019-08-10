package heapdump.record.heapdumpsegment;

import heapdump.FileReader;
import java.io.IOException;
import java.util.Arrays;

public enum BasicType {
  OBJECT(2),
  BOOLEAN(4),
  CHAR(5),
  FLOAT(6),
  DOUBLE(7),
  BYTE(8),
  SHORT(9),
  INT(10),
  LONG(11);

  private final int id;

  BasicType(int id) {
    this.id = id;
  }

  public static BasicType toBasicType(int id) {
    return Arrays.stream(BasicType.values())
        .filter(it -> it.id == id)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown basic type " + id));
  }

  public static long readValue(FileReader reader, BasicType type) throws IOException {
    switch (type) {
      case OBJECT:
        return reader.readId();
      case BOOLEAN:
      case BYTE:
        return reader.read();
      case SHORT:
      case CHAR:
        return reader.readShort();
      case FLOAT:
      case INT:
        return reader.readInt();
      case DOUBLE:
      case LONG:
        return reader.readLong();
      default:
        throw new IllegalArgumentException("Unknown type " + type);
    }
  }
}
