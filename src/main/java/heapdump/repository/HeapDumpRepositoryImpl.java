package heapdump.repository;

import heapdump.record.*;
import heapdump.record.heapdumpsegment.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HeapDumpRepositoryImpl implements HeapDumpRepository {

  private ExecutorService recordProcessor = Executors.newSingleThreadExecutor();

  private Map<Long, String> stringsById = new HashMap<>();
  private Map<Long, String> classNamesByObjectId = new HashMap<>();
  private Map<Long, ClassDefinition> classDefinitionsByObjectId = new HashMap<>();
  private Map<Long, CompletableFuture<JavaObject>> instancesByObjectId = new HashMap<>();
  private Set<JavaObject> allObjects;

  private List<Record> errorRecords = new LinkedList<>();

  public HeapDumpRepositoryImpl() {
    instancesByObjectId.put(0L, CompletableFuture.completedFuture(JavaObject.NULL_VALUE));
  }

  public void addRecord(Record record) {
    if (recordProcessor.isShutdown()) {
      errorRecords.add(record);
      return;
    }
    if (record instanceof StringRecord) {
      recordProcessor.submit(() -> processStringRecord((StringRecord) record));
    } else if (record instanceof LoadClassRecord) {
      recordProcessor.submit(() -> processLoadClassRecord((LoadClassRecord) record));
    } else if (record instanceof StackTraceRecord) {
      // no-op
    } else if (record instanceof StackFrameRecord) {
      // no-op
    } else if (record instanceof ClassDumpRecord) {
      recordProcessor.submit(() -> processClassDumpRecord((ClassDumpRecord) record));
    } else if (record instanceof InstanceDumpRecord) {
      recordProcessor.submit(() -> processInstanceDumpRecord((InstanceDumpRecord) record));
    } else if (record instanceof PrimitiveArrayDumpRecord) {
      recordProcessor.submit(
          () -> processPrimitiveArrayDumpRecord((PrimitiveArrayDumpRecord) record));
    } else if (record instanceof ObjectArrayDumpRecord) {
      recordProcessor.submit(() -> processObjectArrayDumpRecord((ObjectArrayDumpRecord) record));
    } else if (record instanceof RootThreadObjectRecord) {
      // no-op
    } else if (record instanceof RootJavaFrameRecord) {
      // no-op
    } else if (record instanceof RootMonitorUsedRecord) {
      // no-op
    } else if (record instanceof RootJniGlobalRecord) {
      // no-op
    } else if (record instanceof RootStickyClassRecord) {
      // no-op
    } else if (record instanceof HeapDumpSegmentRecord) {
      // no-op
    } else if (record instanceof HeapDumpEndRecord) {
      // no-op
    } else {
      System.err.println("Unable to handle record type " + record.getClass());
      System.exit(1);
    }
  }

  private void processStringRecord(StringRecord stringRecord) {
    stringsById.put(stringRecord.getId(), stringRecord.getValue());
  }

  private void processLoadClassRecord(LoadClassRecord loadClassRecord) {
    String className = stringsById.get(loadClassRecord.getClassNameStringId());
    if (className == null) {
      addRecord(loadClassRecord);
      return;
    }
    classNamesByObjectId.put(loadClassRecord.getClassObjectId(), className);
  }

  private void processClassDumpRecord(ClassDumpRecord classDumpRecord) {
    String name = classNamesByObjectId.get(classDumpRecord.getClassObjectId());
    if (name == null) {
      addRecord(classDumpRecord);
      return;
    }
    ClassDefinition superclass =
        classDefinitionsByObjectId.get(classDumpRecord.getSuperClassObjectId());
    if (superclass == null && classDumpRecord.getSuperClassObjectId() != 0) {
      addRecord(classDumpRecord);
    }
    Map<Short, FieldValue> constantPool = new LinkedHashMap<>();
    for (ClassDumpRecord.ConstantPoolEntry entry : classDumpRecord.getConstantPool()) {
      constantPool.put(
          entry.getConstantPoolIndex(), createFieldValue(entry.getType(), entry.getValue()));
    }
    Map<String, FieldValue> staticFields = new LinkedHashMap<>();
    for (ClassDumpRecord.StaticField field : classDumpRecord.getStaticFields()) {
      String fieldName = stringsById.get(field.getFieldNameStringId());
      if (fieldName == null) {
        addRecord(classDumpRecord);
        return;
      }
      staticFields.put(fieldName, createFieldValue(field.getType(), field.getValue()));
    }
    Map<String, BasicType> instanceFields = new LinkedHashMap<>();
    for (ClassDumpRecord.InstanceField field : classDumpRecord.getInstanceFields()) {
      String fieldName = stringsById.get(field.getFieldNameStringId());
      if (fieldName == null) {
        addRecord(classDumpRecord);
        return;
      }
      instanceFields.put(fieldName, field.getType());
    }
    ClassDefinition classDefinition =
        new ClassDefinition(
            name,
            superclass,
            classDumpRecord.getInstanceSizeBytes(),
            constantPool,
            staticFields,
            instanceFields);
    classDefinitionsByObjectId.put(classDumpRecord.getClassObjectId(), classDefinition);
  }

  private void processInstanceDumpRecord(InstanceDumpRecord instanceDumpRecord) {
    CompletableFuture<JavaObject> future =
        instancesByObjectId.computeIfAbsent(
            instanceDumpRecord.getObjectId(), x -> new CompletableFuture<>());
    ClassDefinition type = classDefinitionsByObjectId.get(instanceDumpRecord.getClassObjectId());
    if (type == null) {
      addRecord(instanceDumpRecord);
      return;
    }
    List<InstanceField> fieldValues = new LinkedList<>();
    InstanceDumpRecord.FieldReader fieldReader = instanceDumpRecord.getFieldReader();
    ClassDefinition currentClass = type;
    while (currentClass != null) {
      ClassDefinition c = currentClass;
      currentClass
          .getInstanceFields()
          .forEach(
              (fieldName, fieldType) -> {
                FieldValue<?> value = createFieldValue(fieldType, fieldReader.readField(fieldType));
                fieldValues.add(new InstanceField(c, fieldName, value));
              });
      currentClass = currentClass.getSuperclass();
    }
    future.complete(new ObjectInstance(type, fieldValues.toArray(new InstanceField[0])));
  }

  private void processObjectArrayDumpRecord(ObjectArrayDumpRecord objectArrayDumpRecord) {
    CompletableFuture<JavaObject> future =
        instancesByObjectId.computeIfAbsent(
            objectArrayDumpRecord.getArrayObjectId(), x -> new CompletableFuture<>());
    ClassDefinition type =
        classDefinitionsByObjectId.get(objectArrayDumpRecord.getArrayClassObjectId());
    if (type == null) {
      addRecord(objectArrayDumpRecord);
      return;
    }
    @SuppressWarnings("unchecked")
    CompletableFuture<JavaObject>[] values =
        Arrays.stream(objectArrayDumpRecord.getElements())
            .mapToObj(val -> createFieldValue(BasicType.OBJECT, val))
            .map(FieldValue::getCompletableValue)
            .toArray(CompletableFuture[]::new);
    future.complete(new ObjectArrayInstance(type, values));
  }

  private void processPrimitiveArrayDumpRecord(PrimitiveArrayDumpRecord primitiveArrayDumpRecord) {
    CompletableFuture<JavaObject> future =
        instancesByObjectId.computeIfAbsent(
            primitiveArrayDumpRecord.getArrayObjectId(), x -> new CompletableFuture<>());
    Class<?> fieldType = getFieldType(primitiveArrayDumpRecord.getElementType());
    Object[] values =
        Arrays.stream(primitiveArrayDumpRecord.getElements())
            .mapToObj(val -> createFieldValue(primitiveArrayDumpRecord.getElementType(), val))
            .map(FieldValue::getValue)
            .toArray(size -> (Object[]) Array.newInstance(fieldType, size));
    future.complete(new PrimitiveArrayInstance<>(fieldType, values));
  }

  private Class<?> getFieldType(BasicType type) {
    switch (type) {
      case OBJECT:
        return JavaObject.class;
      case BOOLEAN:
        return Boolean.class;
      case CHAR:
        return Character.class;
      case FLOAT:
        return Float.class;
      case DOUBLE:
        return Double.class;
      case BYTE:
        return Byte.class;
      case SHORT:
        return Short.class;
      case INT:
        return Integer.class;
      case LONG:
        return Long.class;
      default:
        throw new IllegalArgumentException("Unknown type " + type);
    }
  }

  private FieldValue<?> createFieldValue(BasicType type, long value) {
    switch (type) {
      case OBJECT:
        return createObjectFieldValue(
            instancesByObjectId.computeIfAbsent(value, x -> new CompletableFuture<>()));
      case BOOLEAN:
        return createBooleanFieldValue(value != 0);
      case CHAR:
        return createCharFieldValue((char) value);
      case FLOAT:
        return createFloatFieldValue(Float.intBitsToFloat((int) value));
      case DOUBLE:
        return createDoubleFieldValue(Double.longBitsToDouble(value));
      case BYTE:
        return createByteFieldValue((byte) value);
      case SHORT:
        return createShortFieldValue((short) value);
      case INT:
        return createIntFieldValue((int) value);
      case LONG:
        return createLongFieldValue(value);
      default:
        throw new IllegalArgumentException("Unknown type " + type);
    }
  }

  private static FieldValue<JavaObject> createObjectFieldValue(
      CompletableFuture<JavaObject> future) {
    return new FieldValue<>() {
      @Override
      public Class<JavaObject> getType() {
        return JavaObject.class;
      }

      @Override
      public CompletableFuture<JavaObject> getCompletableValue() {
        return future;
      }
    };
  }

  private static FieldValue<Boolean> createBooleanFieldValue(boolean value) {
    return new FieldValue<>() {
      @Override
      public Class<Boolean> getType() {
        return Boolean.class;
      }

      public Boolean getValue() {
        return value;
      }
    };
  }

  private static FieldValue<Character> createCharFieldValue(char value) {
    return new FieldValue<>() {
      @Override
      public Class<Character> getType() {
        return Character.class;
      }

      public Character getValue() {
        return value;
      }
    };
  }

  private static FieldValue<Float> createFloatFieldValue(float value) {
    return new FieldValue<>() {
      @Override
      public Class<Float> getType() {
        return Float.class;
      }

      public Float getValue() {
        return value;
      }
    };
  }

  private static FieldValue<Double> createDoubleFieldValue(double value) {
    return new FieldValue<>() {
      @Override
      public Class<Double> getType() {
        return Double.class;
      }

      public Double getValue() {
        return value;
      }
    };
  }

  private static FieldValue<Byte> createByteFieldValue(byte value) {
    return new FieldValue<>() {
      @Override
      public Class<Byte> getType() {
        return Byte.class;
      }

      public Byte getValue() {
        return value;
      }
    };
  }

  private static FieldValue<Short> createShortFieldValue(short value) {
    return new FieldValue<>() {
      @Override
      public Class<Short> getType() {
        return Short.class;
      }

      public Short getValue() {
        return value;
      }
    };
  }

  private static FieldValue<Integer> createIntFieldValue(int value) {
    return new FieldValue<>() {
      @Override
      public Class<Integer> getType() {
        return Integer.class;
      }

      public Integer getValue() {
        return value;
      }
    };
  }

  private static FieldValue<Long> createLongFieldValue(long value) {
    return new FieldValue<>() {
      @Override
      public Class<Long> getType() {
        return Long.class;
      }

      public Long getValue() {
        return value;
      }
    };
  }

  public void doneReading() {
    recordProcessor.shutdown();
    try {
      recordProcessor.awaitTermination(1, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
    if (!errorRecords.isEmpty()) {
      throw new RuntimeException("Unable to process records: " + errorRecords);
    }
    stringsById = null;
    classNamesByObjectId = null;
    classDefinitionsByObjectId = null;
    allObjects =
        instancesByObjectId.values().stream()
            .filter(CompletableFuture::isDone)
            .map(CompletableFuture::join)
            .collect(Collectors.toSet());
    instancesByObjectId = null;
  }

  public Stream<String> allStrings() {
    return allObjects.stream()
        .filter(ObjectInstance.class::isInstance)
        .map(ObjectInstance.class::cast)
        .filter(i -> i.getClassDefinition().getName().equals("java/lang/String"))
        .map(i -> i.getField("value"))
        .map(v -> (PrimitiveArrayInstance<Character>) v.getValue())
        .map(
            i -> {
              char[] str = new char[i.size()];
              for (int j = 0; j < i.size(); j++) str[j] = i.get(j);
              return new String(str);
            });
  }
}
