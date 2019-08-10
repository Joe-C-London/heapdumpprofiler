package heapdump.record;

class UnknownRecord implements Record {
  private final int tag;

  UnknownRecord(int tag) {
    this.tag = tag;
  }
}
