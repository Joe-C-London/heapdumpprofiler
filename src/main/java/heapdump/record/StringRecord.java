package heapdump.record;

import heapdump.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StringRecord implements Record {

  public static StringRecord readRecord(FileReader reader, int recordLength) throws IOException {
    long id = reader.readId();
    var bytes = new byte[recordLength - reader.getIdentifierSize()];
    reader.read(bytes);
    String value = new String(bytes, StandardCharsets.UTF_8);
    return new StringRecord(id, value);
  }

  private final long id;
  private final String value;

  private StringRecord(long id, String value) {
    this.id = id;
    this.value = value;
  }

  public long getId() {
    return id;
  }

  public String getValue() {
    return value;
  }
}
