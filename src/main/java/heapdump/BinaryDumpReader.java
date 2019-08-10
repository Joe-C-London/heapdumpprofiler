package heapdump;

import heapdump.record.Record;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BinaryDumpReader {

  public static BinaryDump readHeapDump(File file) throws IOException {
    try (FileReader reader = new FileReader(file)) {
      String formatName = reader.readNullTerminatedString();
      int identifierSize = reader.readInt();
      reader.setIdentifierSize(identifierSize);
      long millisSinceEpoch = reader.readLong();
      List<Record> records = new ArrayList<>();
      BinaryDump ret = new BinaryDump(formatName, identifierSize, millisSinceEpoch, records);
      while (reader.getFilePointer() < reader.length()) {
        records.add(Record.readRecord(reader));
        System.out.print(
            "\r"
                + new DecimalFormat("0.00")
                    .format(100.0 * reader.getFilePointer() / reader.length())
                + "% read");
      }
      System.out.println();
      return ret;
    }
  }
}
