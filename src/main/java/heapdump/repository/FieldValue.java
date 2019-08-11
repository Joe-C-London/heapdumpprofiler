package heapdump.repository;

import java.util.concurrent.CompletableFuture;

public interface FieldValue<T> {

  Class<T> getType();

  CompletableFuture<T> getValue();
}
