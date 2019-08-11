package heapdump;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FileReader implements AutoCloseable {

  private final FileChannel fileChannel;
  private final long totalSize;
  private final MappedByteBuffer[] buffers;
  private int currentBuffer = 0;
  private int identifierSize;

  public FileReader(File file) throws IOException {
    this.fileChannel = new FileInputStream(file).getChannel();
    this.totalSize = fileChannel.size();
    int numSegments = (int) Math.ceil(1.0 * totalSize / Integer.MAX_VALUE);
    this.buffers = new MappedByteBuffer[numSegments];
    for (int i = 0; i < numSegments; i++) {
      long start = ((long) i) * Integer.MAX_VALUE;
      this.buffers[i] =
          fileChannel.map(READ_ONLY, start, Math.min(Integer.MAX_VALUE, totalSize - start));
    }
  }

  public int getIdentifierSize() {
    return this.identifierSize;
  }

  void setIdentifierSize(int identifierSize) {
    this.identifierSize = identifierSize;
  }

  public String readNullTerminatedString() throws IOException {
    StringBuilder str = new StringBuilder();
    int c;
    while (true) {
      c = read();
      if (c == 0) {
        break;
      }
      str.append((char) c);
    }
    return str.toString();
  }

  public void skipBytes(int num) throws IOException {
    read(new byte[num]);
  }

  public int read() throws IOException {
    return readNextByte();
  }

  private byte readNextByte() {
    while (!buffers[currentBuffer].hasRemaining()) {
      currentBuffer++;
    }
    return buffers[currentBuffer].get();
  }

  public void read(byte[] bytes) throws IOException {
    int read = 0;
    while (read < bytes.length) {
      int remaining = buffers[currentBuffer].remaining();
      if (remaining >= bytes.length) {
        buffers[currentBuffer].get(bytes, read, bytes.length - read);
        return;
      }
      buffers[currentBuffer].get(bytes, read, remaining);
      read += remaining;
      currentBuffer++;
    }
  }

  public short readShort() throws IOException {
    byte[] bytes = new byte[2];
    read(bytes);
    short ret = 0;
    for (int i = 0; i < 2; i++) {
      ret <<= 8;
      short next = bytes[i];
      ret |= (next & 0xff);
    }
    return ret;
  }

  public int readInt() throws IOException {
    byte[] bytes = new byte[4];
    read(bytes);
    int ret = 0;
    for (int i = 0; i < 4; i++) {
      ret <<= 8;
      int next = bytes[i];
      ret |= (next & 0xff);
    }
    return ret;
  }

  public long readLong() throws IOException {
    byte[] bytes = new byte[8];
    read(bytes);
    long ret = 0;
    for (int i = 0; i < 8; i++) {
      ret <<= 8;
      long next = bytes[i];
      ret |= (next & 0xff);
    }
    return ret;
  }

  public long readId() throws IOException {
    switch (this.identifierSize) {
      case 1:
        return read();
      case 2:
        return readShort();
      case 4:
        return readInt();
      case 8:
        return readLong();
      default:
        throw new IllegalArgumentException("Unknown ID length " + this.identifierSize);
    }
  }

  public long position() throws IOException {
    return (Integer.MAX_VALUE * (long) currentBuffer) + buffers[currentBuffer].position();
  }

  public long length() throws IOException {
    return totalSize;
  }

  @Override
  public void close() throws IOException {
    fileChannel.close();
  }
}
