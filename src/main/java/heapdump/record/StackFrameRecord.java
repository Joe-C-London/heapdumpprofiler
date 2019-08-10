package heapdump.record;

import heapdump.FileReader;
import java.io.IOException;

public class StackFrameRecord implements Record {

  public static StackFrameRecord readRecord(FileReader reader) throws IOException {
    long stackFrameId = reader.readId();
    long methodNameStringId = reader.readId();
    long methodSignatureStringId = reader.readId();
    long sourceFileNameStringId = reader.readId();
    int classSerialNumber = reader.readInt();
    int lineNumber = reader.readInt();
    return new StackFrameRecord(
        stackFrameId,
        methodNameStringId,
        methodSignatureStringId,
        sourceFileNameStringId,
        classSerialNumber,
        lineNumber);
  }

  private final long stackFrameId;
  private final long methodNameStringId;
  private final long methodSignatureStringId;
  private final long sourceFileNameStringId;
  private final int classSerialNumber;
  private final int lineNumber;

  private StackFrameRecord(
      long stackFrameId,
      long methodNameStringId,
      long methodSignatureStringId,
      long sourceFileNameStringId,
      int classSerialNumber,
      int lineNumber) {
    this.stackFrameId = stackFrameId;
    this.methodNameStringId = methodNameStringId;
    this.methodSignatureStringId = methodSignatureStringId;
    this.sourceFileNameStringId = sourceFileNameStringId;
    this.classSerialNumber = classSerialNumber;
    this.lineNumber = lineNumber;
  }
}
