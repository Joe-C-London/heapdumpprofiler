package heapdump;

import heapdump.record.Record;

public interface RecordListener {

  void onRecordRead(Record record);
}
