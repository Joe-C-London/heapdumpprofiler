package heapdump;

import heapdump.record.Record;
import heapdump.repository.HeapDumpRepository;
import heapdump.repository.HeapDumpRepositoryImpl;
import java.io.File;
import java.io.IOException;

public class BinaryDumpReader {

  public static HeapDumpRepository readHeapDump(File file) throws IOException {
    return readHeapDump(file, (read, total) -> {});
  }

  public static HeapDumpRepository readHeapDump(File file, ProgressListener progressListener)
      throws IOException {
    try (FileReader reader = new FileReader(file)) {
      String formatName = reader.readNullTerminatedString();
      int identifierSize = reader.readInt();
      reader.setIdentifierSize(identifierSize);
      long millisSinceEpoch = reader.readLong();
      HeapDumpRepositoryImpl ret = new HeapDumpRepositoryImpl();
      while (reader.position() < reader.length()) {
        Record.readRecord(reader, ret::addRecord, progressListener);
        progressListener.onProgress(reader.position(), reader.length());
      }
      ret.doneReading();
      return ret;
    }
  }

  public interface ProgressListener {
    void onProgress(long bytesRead, long totalBytes);
  }
}
