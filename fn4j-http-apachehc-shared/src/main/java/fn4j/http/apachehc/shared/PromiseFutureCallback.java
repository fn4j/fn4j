package fn4j.http.apachehc.shared;

import io.vavr.concurrent.Promise;
import org.apache.hc.core5.concurrent.FutureCallback;

import java.util.concurrent.CancellationException;

import static java.util.Objects.requireNonNull;

public final class PromiseFutureCallback<A> implements FutureCallback<A> {
    private final Promise<A> promise;

    private PromiseFutureCallback(Promise<A> promise) {
        this.promise = requireNonNull(promise, "promise");
    }

    public static <A> PromiseFutureCallback<A> futureCallback(Promise<A> promise) {
        return new PromiseFutureCallback<>(promise);
    }

    @Override
    public void completed(A result) {
        promise.success(result);
    }

    @Override
    public void failed(Exception ex) {
        promise.failure(ex);
    }

    @Override
    public void cancelled() {
        promise.failure(new CancellationException());
    }
}