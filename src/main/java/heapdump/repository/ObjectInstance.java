package heapdump.repository;

import java.util.Map;

public class ObjectInstance extends JavaObject {

  private final ClassDefinition classDefinition;
  private final Map<String, FieldValue<?>> fields;

  public ObjectInstance(ClassDefinition classDefinition, Map<String, FieldValue<?>> fields) {
    this.classDefinition = classDefinition;
    this.fields = fields;
  }

  public ClassDefinition getClassDefinition() {
    return classDefinition;
  }

  public Map<String, FieldValue<?>> getFields() {
    return fields;
  }
}
