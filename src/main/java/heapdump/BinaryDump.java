package heapdump;

import heapdump.record.Record;
import java.util.List;

public class BinaryDump {
  private final String formatName;
  private final int identifierSize;
  private final long millisSinceEpoch;
  private final List<Record> records;

  public BinaryDump(
      String formatName, int identifierSize, long millisSinceEpoch, List<Record> records) {
    this.formatName = formatName;
    this.identifierSize = identifierSize;
    this.millisSinceEpoch = millisSinceEpoch;
    this.records = records;
  }
}
