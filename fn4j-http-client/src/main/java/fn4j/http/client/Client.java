package fn4j.http.client;

import fn4j.http.Request;
import fn4j.http.Response;
import io.vavr.concurrent.Future;

public interface Client {
    Future<Response<byte[]>> exchange(Request<byte[]> request);
}