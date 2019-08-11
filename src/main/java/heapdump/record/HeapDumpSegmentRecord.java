package heapdump.record;

import heapdump.BinaryDumpReader;
import heapdump.FileReader;
import heapdump.RecordListener;
import heapdump.record.heapdumpsegment.SubRecord;
import java.io.IOException;

public class HeapDumpSegmentRecord implements Record {

  public static HeapDumpSegmentRecord readRecord(
      FileReader reader,
      int recordLength,
      RecordListener recordListener,
      BinaryDumpReader.ProgressListener progressListener)
      throws IOException {
    long end = reader.position() + recordLength;
    if (recordLength < 0) {
      end += (((long) 1) << 32);
    }
    while (reader.position() < end) {
      SubRecord.readRecord(reader, recordListener);
      progressListener.onProgress(reader.position(), reader.length());
    }
    return new HeapDumpSegmentRecord();
  }

  private HeapDumpSegmentRecord() {}
}
