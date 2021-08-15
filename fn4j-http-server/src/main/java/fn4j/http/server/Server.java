package fn4j.http.server;

import io.vavr.concurrent.Future;

@FunctionalInterface
public interface Server {
    Future<Closer> open();

    interface Closer extends AutoCloseable {
        @Override
        void close();

        Future<Void> awaitClose();
    }
}