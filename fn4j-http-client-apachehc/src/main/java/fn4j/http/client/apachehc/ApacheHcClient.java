package fn4j.http.client.apachehc;

import fn4j.http.apachehc.shared.ApacheHc2Fn4j;
import fn4j.http.client.Client;
import fn4j.http.client.RequestTimeout;
import fn4j.http.core.Request;
import fn4j.http.core.Response;
import io.vavr.concurrent.Future;
import io.vavr.concurrent.Promise;
import io.vavr.control.Option;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.impl.bootstrap.AsyncRequesterBootstrap;
import org.apache.hc.core5.http.impl.bootstrap.HttpAsyncRequester;
import org.apache.hc.core5.http.nio.entity.BasicAsyncEntityConsumer;
import org.apache.hc.core5.http.nio.support.BasicResponseConsumer;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;

import static fn4j.http.apachehc.shared.Fn4j2ApacheHc.asyncRequestProducer;
import static fn4j.http.apachehc.shared.PromiseFutureCallback.futureCallback;

public class ApacheHcClient implements Client, AutoCloseable {
    private final Timeout timeout;
    private final HttpAsyncRequester httpAsyncRequester;

    public ApacheHcClient(Option<RequestTimeout> maybeRequestTimeout) {
        this.timeout = maybeRequestTimeout.fold(() -> Timeout.DISABLED,
                                                requestTimeout -> Timeout.ofNanoseconds(requestTimeout.value().toNanos()));
        httpAsyncRequester = AsyncRequesterBootstrap.bootstrap().create();
    }

    public static ApacheHcClientBuilder builder() {
        return new ApacheHcClientBuilder();
    }

    @Override
    public Future<Response<byte[]>> exchange(Request<byte[]> request) {
        var promise = Promise.<Message<HttpResponse, byte[]>>make();
        httpAsyncRequester.execute(asyncRequestProducer(request),
                                   new BasicResponseConsumer<>(new BasicAsyncEntityConsumer()),
                                   timeout,
                                   futureCallback(promise));
        return promise.future().map(ApacheHc2Fn4j::response);
    }

    @Override
    public void close() throws IOException {
        httpAsyncRequester.close();
    }
}