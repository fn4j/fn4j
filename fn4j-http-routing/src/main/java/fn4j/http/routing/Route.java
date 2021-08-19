package fn4j.http.routing;

import fn4j.http.core.Method;
import fn4j.net.uri.Path;
import io.vavr.collection.Seq;

import java.util.function.Function;

public record Route<P, A, B>(Method method,
                             PathPattern<P> pathPattern,
                             Function<? super P, ? extends Handler<A, B>> parameterizedHandler) {
    public Route<P, A, B> compile() {
        return new Route<>(method,
                           pathPattern.compile(),
                           parameterizedHandler);
    }

    public static <A, B> Route<?, A, B> route(Method method,
                                              Handler<A, B> handler) {
        return route(method, PathPattern.pathPattern(), handler);
    }

    public static <A, B> Route<?, A, B> route(Method method,
                                              String path,
                                              Handler<A, B> handler) {
        return route(method, new Path(path), handler);
    }

    public static <A, B> Route<?, A, B> route(Method method,
                                              Path path,
                                              Handler<A, B> handler) {
        return route(method, PathPattern.pathPattern(path), handler);
    }

    public static <P, A, B> Route<?, A, B> route(Method method,
                                                 PathPattern<P> pathPattern,
                                                 Handler<A, B> handler) {
        return route(method, pathPattern, (Function<? super P, ? extends Handler<A, B>>) __ -> handler);
    }

    public static <P, A, B> Route<P, A, B> route(Method method,
                                                 PathPattern<P> pathPattern,
                                                 Function<? super P, ? extends Handler<A, B>> parameterizedHandler) {
        return new Route<>(method, pathPattern, parameterizedHandler);
    }

    public static <P, A, B> Route<?, A, B> route(String path,
                                                 Route<P, A, B> route) {
        return route(new Path(path), route);
    }

    public static <P, A, B> Route<P, A, B> route(Path path,
                                                 Route<P, A, B> route) {
        return route(route.method(),
                     PathPattern.pathPattern(path).slash(route.pathPattern()),
                     (Function<P, Handler<A, B>>) parameter -> route.parameterizedHandler().apply(parameter));
    }

    public static <A, B> Seq<Route<?, A, B>> route(String path,
                                                   Seq<Route<?, A, B>> routes) {
        return route(new Path(path), routes);
    }

    public static <A, B> Seq<Route<?, A, B>> route(Path path,
                                                   Seq<Route<?, A, B>> routes) {
        return routes.map(route -> route(path, route));
    }
}