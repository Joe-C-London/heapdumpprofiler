import heapdump.BinaryDump;
import heapdump.BinaryDumpReader;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class HeapDumpSample {

  public static void main(String[] args) throws IOException {
    Instant start = Instant.now();
    BinaryDump dump = BinaryDumpReader.readHeapDump(new File(args[0]));
    Instant end = Instant.now();
    System.out.println(Duration.between(start, end));
    System.out.println(dump);
  }
}
