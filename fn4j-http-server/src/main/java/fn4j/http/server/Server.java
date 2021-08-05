package fn4j.http.server;

import io.vavr.concurrent.Future;

import java.net.SocketAddress;

@FunctionalInterface
public interface Server {
    Future<Closer> open(SocketAddress socketAddress);

    interface Closer {
        void close();

        Future<Void> awaitClose();
    }
}