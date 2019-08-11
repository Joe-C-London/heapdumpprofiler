import heapdump.BinaryDumpReader;
import heapdump.repository.HeapDumpRepository;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class HeapDumpSample {

  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

  public static void main(String[] args) throws IOException {
    Instant start = Instant.now();
    BinaryDumpReader.ProgressListener progressListener =
        new BinaryDumpReader.ProgressListener() {
          private Instant lastUpdate = Instant.ofEpochMilli(0);
          private Instant start = Instant.now();

          @Override
          public void onProgress(long read, long total) {
            if (read == total || lastUpdate.until(Instant.now(), ChronoUnit.MILLIS) > 100) {
              long timePassed = start.until(Instant.now(), ChronoUnit.MILLIS);
              long totalTime = (long) (timePassed * (1.0 * total / read));
              long timeRemaing = totalTime - timePassed;
              System.out.print(
                  "\rRead "
                      + formatBytes(read)
                      + " of "
                      + formatBytes(total)
                      + " ("
                      + DECIMAL_FORMAT.format(100.0 * read / total)
                      + "%), "
                      + formatTime(timeRemaing)
                      + " remaining\t\t");
              lastUpdate = Instant.now();
            }
          }
        };
    HeapDumpRepository dump = BinaryDumpReader.readHeapDump(new File(args[0]), progressListener);
    System.out.println();
    Instant end = Instant.now();
    System.out.println(Duration.between(start, end));
    System.out.println(dump);
  }

  private static String formatTime(long millis) {
    Duration duration = Duration.of(millis, ChronoUnit.MILLIS);
    String ret = "";
    if (duration.toHours() > 0) {
      ret += duration.toHours();
    }
    if (!ret.isEmpty()) {
      ret += ":";
      if (duration.toMinutesPart() < 10) ret += "0";
    }
    ret += duration.toMinutesPart();
    ret += ":";
    if (duration.toSecondsPart() < 10) ret += "0";
    ret += duration.toSecondsPart();
    return ret;
  }

  private static String formatBytes(long bytes) {
    if (bytes > 1024 * 1024 * 1024) {
      return DECIMAL_FORMAT.format(1.0 * bytes / 1024 / 1024 / 1024) + " GB";
    }
    if (bytes > 1024 * 1024) {
      return DECIMAL_FORMAT.format(1.0 * bytes / 1024 / 1024) + " MB";
    }
    if (bytes > 1024) {
      return DECIMAL_FORMAT.format(1.0 * bytes / 1024) + " kB";
    }
    return bytes + " B";
  }
}
