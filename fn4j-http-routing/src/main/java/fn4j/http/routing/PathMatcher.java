package fn4j.http.routing;

import fn4j.http.core.Headers;
import fn4j.http.core.Request;
import fn4j.http.core.Response;
import fn4j.net.uri.Path;
import io.vavr.collection.Stream;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;

import java.util.function.Function;

import static fn4j.http.core.ResponseHead.responseHead;
import static fn4j.http.core.Status.NOT_FOUND;

public record PathMatcher<A, B>(Stream<Case<A, ?, B>> cases) implements PartialHandler<A, B> {
    public PathMatcher(Iterable<Case<A, ?, B>> cases) {
        this(Stream.ofAll(cases));
    }

    @SafeVarargs
    public static <A, B> PathMatcher<A, B> matchPath(Case<A, ?, B> firstCase,
                                                     Case<A, ?, B>... furtherCases) {
        return matchPath(Stream.of(furtherCases).prepend(firstCase));
    }

    public static <A, B> PathMatcher<A, B> matchPath(Iterable<Case<A, ?, B>> cases) {
        return new PathMatcher<>(cases);
    }

    public static record Case<A, P, B>(PathPattern<P> pathPattern,
                                       Function<? super P, ? extends Handler<A, B>> parameterizedHandler) {
    }

    @Override
    public Option<Future<Response<B>>> apply(Request<A> request) {
        return firstMatch(request).map(handler -> handler.apply(request));
    }

    @Override
    public boolean isDefinedFor(Request<A> request) {
        return firstMatch(request).isDefined();
    }

    public boolean isDefinedFor(Path path) {
        return firstMatch(path).isDefined();
    }

    public Handler<A, B> orNotFound() {
        return orElse(notFound());
    }

    private Option<? extends Handler<A, B>> firstMatch(Request<A> request) {
        return firstMatch(request.uri().path());
    }

    @SuppressWarnings("unchecked")
    private Option<? extends Handler<A, B>> firstMatch(Path path) {
        return cases.flatMap(_case -> {
                        var safe = (Case<A, Object, B>) _case;
                        var handler = safe.parameterizedHandler();
                        return safe.pathPattern()
                                   .apply(path)
                                   .map(handler)
                                   .toStream();
                    })
                    .headOption();
    }

    private Handler<A, B> notFound() {
        return request -> Future.successful(responseHead(NOT_FOUND, Headers.empty()).toResponseWithoutBody());
    }
}