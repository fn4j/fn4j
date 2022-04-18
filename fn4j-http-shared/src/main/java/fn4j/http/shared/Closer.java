package fn4j.http.shared;

import io.vavr.concurrent.Future;

public interface Closer extends AutoCloseable {
    @Override
    void close();

    Future<Void> onClose();

    default Future<Void> awaitClose() {
        return onClose().await();
    }
}