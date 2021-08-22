package fn4j.http.routing;

import fn4j.http.answering.Handler;
import fn4j.http.answering.PartialHandler;
import fn4j.http.core.Method;
import fn4j.http.core.Request;
import fn4j.http.core.RequestHead;
import fn4j.http.core.Response;
import fn4j.http.core.header.HeaderValue;
import fn4j.http.core.header.Headers;
import io.vavr.Tuple;
import io.vavr.collection.*;
import io.vavr.concurrent.Future;
import io.vavr.control.Either;
import io.vavr.control.Option;

import java.util.function.Function;

import static fn4j.http.core.Response.response;
import static fn4j.http.core.Status.METHOD_NOT_ALLOWED;
import static fn4j.http.core.Status.NOT_FOUND;
import static fn4j.http.core.header.HeaderName.ALLOW;
import static fn4j.http.core.header.Headers.headers;

public record Router<A, B>(Seq<Route<?, A, B>> routes) implements Handler<A, B> {
    @SafeVarargs
    public static <A, B> Router<A, B> router(Route<?, A, B>... routes) {
        return new Router<>(Array.narrow(Stream.of(routes).map(Route::compile).toArray()));
    }

    @SafeVarargs
    public static <A, B> Router<A, B> router(Seq<Route<?, A, B>>... routes) {
        return new Router<>(Array.narrow(Stream.concat(routes).map(Route::compile).toArray()));
    }

    @Override
    public Future<Response<B>> apply(Request<A> request) {
        return handlerFor(request).apply(request);
    }

    public Option<Future<Response<B>>> partiallyApply(Request<A> request) {
        return partialHandlerFor(request).apply(request);
    }

    public Handler<A, B> handlerFor(RequestHead requestHead) {
        return handlerOrAllowedMethodsFor(requestHead).swap().getOrElseGet(this::fallback);
    }

    public PartialHandler<A, B> partialHandlerFor(RequestHead requestHead) {
        return PartialHandler.of(handlerOrAllowedMethodsFor(requestHead).swap().toOption());
    }

    /**
     * @return First matching handler or all methods of handlers where the path matches but the method does not
     */
    @SuppressWarnings("unchecked")
    private Either<Handler<A, B>, SortedSet<Method>> handlerOrAllowedMethodsFor(RequestHead requestHead) {
        Either<Handler<A, B>, SortedSet<Method>> noAllowedMethods = Either.right(TreeSet.empty());
        return routes.foldLeft(noAllowedMethods, (either, route) -> either.flatMap(allowedMethods -> {
            var pathPattern = (PathPattern<Object>) route.pathPattern();
            var parameterizedHandler = (Function<Object, ? extends Handler<A, B>>) route.parameterizedHandler();
            var maybeParameter = pathPattern.apply(requestHead.uri().path());
            return maybeParameter.fold(() -> either, parameter -> {
                if (route.method().equals(requestHead.method())) {
                    Handler<A, B> handler = parameterizedHandler.apply(parameter);
                    return Either.left(handler);
                }
                return Either.right(allowedMethods.add(route.method()));
            });
        }));
    }

    private Handler<A, B> fallback(SortedSet<Method> allowedMethods) {
        return allowedMethods.nonEmpty() ? methodNotAllowed(allowedMethods) : notFound();
    }

    private Handler<A, B> methodNotAllowed(SortedSet<Method> allowedMethods) {
        return request -> {
            var allowHeaderValue = new HeaderValue(allowedMethods.map(Method::value).mkString(","));
            return Future.successful(response(METHOD_NOT_ALLOWED, headers(Tuple.of(ALLOW, allowHeaderValue))));
        };
    }

    private Handler<A, B> notFound() {
        return request -> Future.successful(response(NOT_FOUND, Headers.empty()));
    }
}