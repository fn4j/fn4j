package fn4j.http.server.apachehc;

import fn4j.http.core.Request;
import fn4j.http.core.Response;
import fn4j.http.server.ServerBuilder;
import io.vavr.concurrent.Future;

import java.util.function.Function;

public class ApacheHcServerBuilder implements ServerBuilder {
    @Override
    public ApacheHcServer build(Function<? super Request<byte[]>, ? extends Future<Response<byte[]>>> handler) {
        return new ApacheHcServer(handler);
    }
}