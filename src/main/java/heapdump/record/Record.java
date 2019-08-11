package heapdump.record;

import heapdump.BinaryDumpReader;
import heapdump.FileReader;
import heapdump.RecordListener;
import java.io.IOException;

public interface Record {

  static void readRecord(
      FileReader reader,
      RecordListener recordListener,
      BinaryDumpReader.ProgressListener progressListener)
      throws IOException {
    long start = reader.position();
    int tag = reader.read();
    int millisSinceHeader = reader.readInt();
    int recordLength = reader.readInt();
    Record record;
    switch (tag) {
      case 1:
        record = StringRecord.readRecord(reader, recordLength);
        break;
      case 2:
        record = LoadClassRecord.readRecord(reader);
        break;
      case 4:
        record = StackFrameRecord.readRecord(reader);
        break;
      case 5:
        record = StackTraceRecord.readRecord(reader);
        break;
      case 12:
      case 28:
        record =
            HeapDumpSegmentRecord.readRecord(
                reader, recordLength, recordListener, progressListener);
        break;
      case 44:
        record = new HeapDumpEndRecord();
        break;
      default:
        record = new UnknownRecord(tag);
        reader.skipBytes(recordLength);
    }
    recordListener.onRecordRead(record);
  }
}
