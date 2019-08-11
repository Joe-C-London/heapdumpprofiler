package heapdump.repository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public interface FieldValue<T> {

  Class<T> getType();

  default CompletableFuture<T> getCompletableValue() {
    return CompletableFuture.completedFuture(getValue());
  }

  default T getValue() {
    return getCompletableValue().orTimeout(0, TimeUnit.SECONDS).join();
  }
}
