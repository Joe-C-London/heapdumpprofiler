package heapdump.record;

import heapdump.FileReader;
import heapdump.record.heapdumpsegment.SubRecord;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HeapDumpSegmentRecord implements Record {

  public static HeapDumpSegmentRecord readRecord(FileReader reader, int recordLength)
      throws IOException {
    long end = reader.getFilePointer() + recordLength;
    if (recordLength < 0) {
      end += (((long)1) << 32);
    }
    List<SubRecord> records = new ArrayList<>();
    while (reader.getFilePointer() < end) {
      records.add(SubRecord.readRecord(reader));
      System.out.print(
          "\r"
              + new DecimalFormat("0.00").format(100.0 * reader.getFilePointer() / reader.length())
              + "% read");
    }
    return new HeapDumpSegmentRecord(records);
  }

  private final List<SubRecord> records;

  private HeapDumpSegmentRecord(List<SubRecord> records) {
    this.records = records;
  }
}
