package fn4j.http.server;

import fn4j.http.core.Request;
import fn4j.http.core.Response;
import io.vavr.concurrent.Future;

import java.util.function.Function;

@FunctionalInterface
public interface ServerBuilder {
    Server build(Function<? super Request<byte[]>, ? extends Future<Response<byte[]>>> handler);
}