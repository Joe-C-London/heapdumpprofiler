package heapdump.repository;

public class InstanceField {

  private final ClassDefinition containingClass;
  private final String fieldName;
  private final FieldValue<?> fieldValue;

  public InstanceField(
      ClassDefinition containingClass, String fieldName, FieldValue<?> fieldValue) {
    this.containingClass = containingClass;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }

  public ClassDefinition getContainingClass() {
    return containingClass;
  }

  public String getFieldName() {
    return fieldName;
  }

  public FieldValue<?> getFieldValue() {
    return fieldValue;
  }
}
