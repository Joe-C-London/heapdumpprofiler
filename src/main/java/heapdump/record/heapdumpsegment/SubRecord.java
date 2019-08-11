package heapdump.record.heapdumpsegment;

import heapdump.FileReader;
import heapdump.RecordListener;
import heapdump.record.Record;
import java.io.IOException;

public interface SubRecord extends Record {

  static void readRecord(FileReader reader, RecordListener recordListener) throws IOException {
    long start = reader.position();
    int tag = reader.read();
    SubRecord record;
    switch (tag) {
      case 1:
        record = RootJniGlobalRecord.readRecord(reader);
        break;
      case 2:
        record = RootJniLocalRecord.readRecord(reader);
        break;
      case 3:
        record = RootJavaFrameRecord.readRecord(reader);
        break;
      case 4:
        record = RootNativeStackRecord.readRecord(reader);
        break;
      case 5:
        record = RootStickyClassRecord.readRecord(reader);
        break;
      case 6:
        record = RootThreadBlockRecord.readRecord(reader);
        break;
      case 7:
        record = RootMonitorUsedRecord.readRecord(reader);
        break;
      case 8:
        record = RootThreadObjectRecord.readRecord(reader);
        break;
      case 32:
        record = ClassDumpRecord.readRecord(reader);
        break;
      case 33:
        record = InstanceDumpRecord.readRecord(reader);
        break;
      case 34:
        record = ObjectArrayDumpRecord.readRecord(reader);
        break;
      case 35:
        record = PrimitiveArrayDumpRecord.readRecord(reader);
        break;
      default:
        throw new IllegalArgumentException("Unknown tag " + tag);
    }
    recordListener.onRecordRead(record);
  }
}
