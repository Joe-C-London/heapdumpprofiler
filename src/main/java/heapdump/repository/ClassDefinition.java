package heapdump.repository;

import heapdump.record.heapdumpsegment.BasicType;
import java.util.Map;

public class ClassDefinition {

  private final String name;
  private final ClassDefinition superclass;
  private final int instanceSize;
  private final Map<Short, FieldValue> constantPool;
  private final Map<String, FieldValue> staticFields;
  private final Map<String, BasicType> instanceFields;

  public ClassDefinition(
      String name,
      ClassDefinition superclass,
      int instanceSize,
      Map<Short, FieldValue> constantPool,
      Map<String, FieldValue> staticFields,
      Map<String, BasicType> instanceFields) {
    this.name = name;
    this.superclass = superclass;
    this.instanceSize = instanceSize;
    this.constantPool = constantPool;
    this.staticFields = staticFields;
    this.instanceFields = instanceFields;
  }

  public String getName() {
    return name;
  }

  public ClassDefinition getSuperclass() {
    return superclass;
  }

  public int getInstanceSize() {
    return instanceSize;
  }

  public Map<Short, FieldValue> getConstantPool() {
    return constantPool;
  }

  public Map<String, FieldValue> getStaticFields() {
    return staticFields;
  }

  public Map<String, BasicType> getInstanceFields() {
    return instanceFields;
  }

  @Override
  public String toString() {
    return name;
  }
}
