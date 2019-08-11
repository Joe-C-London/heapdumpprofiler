package heapdump.repository;

import java.util.Arrays;
import java.util.stream.Stream;

public class PrimitiveArrayInstance<T> extends JavaObject {

  private final Class<? extends T> type;
  private final T[] elements;

  public PrimitiveArrayInstance(Class<? extends T> type, T[] elements) {
    this.type = type;
    this.elements = elements;
  }

  public Class<? extends T> getType() {
    return type;
  }

  public int size() {
    return elements.length;
  }

  public T get(int index) {
    return elements[index];
  }

  public Stream<T> stream() {
    return Arrays.stream(elements);
  }

  @Override
  public String toString() {
    return type.getSimpleName() + "[" + elements.length + "]";
  }
}
