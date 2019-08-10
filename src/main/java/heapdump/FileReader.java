package heapdump;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FileReader implements AutoCloseable {

  private final FileChannel fileChannel;
  private final MappedByteBuffer buffer;
  private int identifierSize;

  public FileReader(File file) throws IOException {
    this.fileChannel = new FileInputStream(file).getChannel();
    this.buffer = fileChannel.map(READ_ONLY, 0, fileChannel.size());
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
    buffer.get(new byte[num]);
  }

  public int read() throws IOException {
    return buffer.get();
  }

  public void read(byte[] bytes) throws IOException {
    buffer.get(bytes);
  }

  public short readShort() throws IOException {
    short ret = 0;
    for (int i = 0; i < 2; i++) {
      ret <<= 8;
      short next = buffer.get();
      ret |= (next & 0xff);
    }
    return ret;
  }

  public int readInt() throws IOException {
    int ret = 0;
    for (int i = 0; i < 4; i++) {
      ret <<= 8;
      int next = buffer.get();
      ret |= (next & 0xff);
    }
    return ret;
  }

  public long readLong() throws IOException {
    long ret = 0;
    for (int i = 0; i < 8; i++) {
      ret <<= 8;
      long next = buffer.get();
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

  public long getFilePointer() throws IOException {
    return buffer.position();
  }

  public long length() throws IOException {
    return fileChannel.size();
  }

  @Override
  public void close() throws IOException {
    fileChannel.close();
  }
}
