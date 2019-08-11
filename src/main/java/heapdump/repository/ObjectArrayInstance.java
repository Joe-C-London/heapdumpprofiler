package heapdump.repository;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ObjectArrayInstance extends JavaObject {

  private final ClassDefinition classDefinition;
  private final CompletableFuture<JavaObject[]> elements;

  public ObjectArrayInstance(
      ClassDefinition classDefinition, CompletableFuture<JavaObject>[] elements) {
    this.classDefinition = classDefinition;
    this.elements =
        CompletableFuture.allOf(elements)
            .thenApply(
                x ->
                    Arrays.stream(elements)
                        .map(CompletableFuture::join)
                        .toArray(JavaObject[]::new));
  }

  public ClassDefinition getElementType() {
    return classDefinition;
  }

  public int size() {
    return getElements().length;
  }

  public JavaObject get(int index) {
    return getElements()[index];
  }

  private JavaObject[] getElements() {
    return elements.orTimeout(0, TimeUnit.SECONDS).join();
  }

  public Stream<JavaObject> stream() {
    return Arrays.stream(getElements());
  }

  @Override
  public String toString() {
    return classDefinition + "[" + getElements().length + "]";
  }
}
