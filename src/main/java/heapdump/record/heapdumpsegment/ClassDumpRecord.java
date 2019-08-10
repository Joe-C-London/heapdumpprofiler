package heapdump.record.heapdumpsegment;

import static heapdump.record.heapdumpsegment.BasicType.readValue;
import static heapdump.record.heapdumpsegment.BasicType.toBasicType;

import heapdump.FileReader;
import java.io.IOException;

public class ClassDumpRecord implements SubRecord {

  public static ClassDumpRecord readRecord(FileReader reader) throws IOException {
    long classObjectId = reader.readId();
    int stackTraceSerialNumber = reader.readInt();
    long superClassObjectId = reader.readId();
    long classLoaderObjectId = reader.readId();
    long signersObjectId = reader.readId();
    long protectionDomainObjectId = reader.readId();
    reader.skipBytes(2 * reader.getIdentifierSize());
    int instanceSizeBytes = reader.readInt();

    ConstantPoolEntry[] constantPool = new ConstantPoolEntry[reader.readShort()];
    for (int i = 0; i < constantPool.length; i++) {
      short constantPoolIndex = reader.readShort();
      BasicType type = toBasicType(reader.read());
      long value = readValue(reader, type);
      constantPool[i] = new ConstantPoolEntry(constantPoolIndex, type, value);
    }

    StaticField[] staticFields = new StaticField[reader.readShort()];
    for (int i = 0; i < staticFields.length; i++) {
      long fieldNameStringId = reader.readId();
      BasicType type = toBasicType(reader.read());
      long value = readValue(reader, type);
      staticFields[i] = new StaticField(fieldNameStringId, type, value);
    }

    InstanceField[] instanceFields = new InstanceField[reader.readShort()];
    for (int i = 0; i < instanceFields.length; i++) {
      long fieldNameStringId = reader.readId();
      BasicType type = toBasicType(reader.read());
      instanceFields[i] = new InstanceField(fieldNameStringId, type);
    }

    return new ClassDumpRecord(
        classObjectId,
        stackTraceSerialNumber,
        superClassObjectId,
        classLoaderObjectId,
        signersObjectId,
        protectionDomainObjectId,
        instanceSizeBytes,
        constantPool,
        staticFields,
        instanceFields);
  }

  private final long classObjectId;
  private final int stackTraceSerialNumber;
  private final long superClassObjectId;
  private final long classLoaderObjectId;
  private final long signersObjectId;
  private final long protectionDomainObjectId;
  private final int instanceSizeBytes;
  private final ConstantPoolEntry[] constantPool;
  private final StaticField[] staticFields;
  private final InstanceField[] instanceFields;

  private ClassDumpRecord(
      long classObjectId,
      int stackTraceSerialNumber,
      long superClassObjectId,
      long classLoaderObjectId,
      long signersObjectId,
      long protectionDomainObjectId,
      int instanceSizeBytes,
      ConstantPoolEntry[] constantPool,
      StaticField[] staticFields,
      InstanceField[] instanceFields) {
    this.classObjectId = classObjectId;
    this.stackTraceSerialNumber = stackTraceSerialNumber;
    this.superClassObjectId = superClassObjectId;
    this.classLoaderObjectId = classLoaderObjectId;
    this.signersObjectId = signersObjectId;
    this.protectionDomainObjectId = protectionDomainObjectId;
    this.instanceSizeBytes = instanceSizeBytes;
    this.constantPool = constantPool;
    this.staticFields = staticFields;
    this.instanceFields = instanceFields;
  }

  public static class ConstantPoolEntry {
    private final short constantPoolIndex;
    private final BasicType type;
    private final long value;

    public ConstantPoolEntry(short constantPoolIndex, BasicType type, long value) {
      this.constantPoolIndex = constantPoolIndex;
      this.type = type;
      this.value = value;
    }
  }

  public static class StaticField {
    private final long fieldNameStringId;
    private final BasicType type;
    private final long value;

    public StaticField(long fieldNameStringId, BasicType type, long value) {
      this.fieldNameStringId = fieldNameStringId;
      this.type = type;
      this.value = value;
    }
  }

  public static class InstanceField {
    private final long fieldNameStringId;
    private final BasicType type;

    public InstanceField(long fieldNameStringId, BasicType type) {
      this.fieldNameStringId = fieldNameStringId;
      this.type = type;
    }
  }
}
