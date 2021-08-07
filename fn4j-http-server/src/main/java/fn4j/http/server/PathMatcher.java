package fn4j.http.server;

import fn4j.http.core.Headers;
import fn4j.http.core.Request;
import fn4j.http.core.RequestPath;
import fn4j.http.core.Response;
import io.vavr.collection.Seq;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;

import java.util.function.Function;

import static fn4j.http.core.ResponseHead.responseHead;
import static fn4j.http.core.Status.NOT_FOUND;

public class PathMatcher<A, B> implements PartialHandler<A, B> {
    private final Seq<Case<A, ?, B>> cases;

    public PathMatcher(Seq<Case<A, ?, B>> cases) {
        this.cases = cases;
    }

    @Override
    public Option<Future<Response<B>>> apply(Request<A> request) {
        return firstMatch(request).map(handler -> handler.apply(request));
    }

    public Handler<A, B> orNotFound() {
        return orElse(notFound());
    }

    @SuppressWarnings("unchecked")
    private Option<Handler<A, B>> firstMatch(Request<A> request) {
        return cases.toStream()
                    .flatMap(_case -> {
                        var safe = (Case<A, Object, B>) _case;
                        var handler = safe.handler();
                        return safe.pathExtractor()
                                   .apply(request.requestUri().path())
                                   .map(handler)
                                   .toStream();
                    })
                    .headOption();
    }

    private Handler<A, B> notFound() {
        return request -> Future.successful(responseHead(NOT_FOUND, Headers.empty()).toResponseWithoutBody());
    }

    public static record Case<A, T, B>(Function<RequestPath, ? extends Option<T>> pathExtractor,
                                       Function<? super T, Handler<A, B>> handler) {
    }
}