package fn4j.http.server;

import fn4j.http.shared.Closer;
import io.vavr.concurrent.Future;

public interface Server {
    Future<Closer> open();
}