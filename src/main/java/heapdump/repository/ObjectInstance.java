package heapdump.repository;

import java.util.Arrays;

public class ObjectInstance extends JavaObject {

  private final ClassDefinition classDefinition;
  private final InstanceField[] fields;

  public ObjectInstance(ClassDefinition classDefinition, InstanceField[] fields) {
    this.classDefinition = classDefinition;
    this.fields = fields;
  }

  public ClassDefinition getClassDefinition() {
    return classDefinition;
  }

  public FieldValue<?> getField(String name) {
    return Arrays.stream(fields)
        .filter(f -> f.getFieldName().equals(name))
        .findFirst()
        .map(InstanceField::getFieldValue)
        .orElse(null);
  }
}
