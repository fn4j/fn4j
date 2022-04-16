package fn4j.http.client;

import fn4j.http.core.Request;
import fn4j.http.core.Response;
import io.vavr.concurrent.Future;

@FunctionalInterface
public interface Client {
    Future<Response<byte[]>> exchange(Request<byte[]> request);
}