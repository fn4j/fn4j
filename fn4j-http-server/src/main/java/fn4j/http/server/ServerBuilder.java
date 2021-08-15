package fn4j.http.server;

import fn4j.http.core.Request;
import fn4j.http.core.Response;
import io.vavr.concurrent.Future;

import java.util.function.Function;

public interface ServerBuilder<S extends Server, SELF extends ServerBuilder<S, SELF>> {
    SELF handler(Function<? super Request<byte[]>, ? extends Future<Response<byte[]>>> handler);

    S build();
}