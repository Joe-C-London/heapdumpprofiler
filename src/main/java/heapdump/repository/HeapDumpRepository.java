package heapdump.repository;

import java.util.stream.Stream;

public interface HeapDumpRepository {

  Stream<String> allStrings();
}
