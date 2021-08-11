package fn4j.http.routing;

import fn4j.http.core.Method;
import fn4j.http.core.Path;
import fn4j.http.core.Request;
import fn4j.http.core.Response;
import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.Stream;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;

import java.util.function.Function;

@FunctionalInterface
public interface Handler<A, B> extends Function1<Request<A>, Future<Response<B>>> {
    Future<Response<B>> apply(Request<A> request);

    default <C> Handler<A, C> pipe(PostProcessor<B, C> postProcessor) {
        return request -> apply(request).map(postProcessor);
    }

    static <A, B> Handler<A, B> of(Function<? super Request<A>, ? extends Future<Response<B>>> handler) {
        return handler::apply;
    }

    @SafeVarargs
    static <A, B> PathMatcher<A, B> matchPath(PathMatcher.Case<A, ?, B> firstCase,
                                              PathMatcher.Case<A, ?, B>... furtherCases) {
        return new PathMatcher<>(Stream.of(furtherCases).prepend(firstCase));
    }

    static <A, T, B> PathMatcher.Case<A, T, B> pathCase(Function<Path, ? extends Option<T>> pathExtractor,
                                                        Function<? super T, Handler<A, B>> handler) {
        return new PathMatcher.Case<>(pathExtractor, handler);
    }

    @SafeVarargs
    static <A, B> MethodMatcher<A, B> matchMethod(Tuple2<Method, Handler<A, B>> firstCase,
                                                  Tuple2<Method, Handler<A, B>>... furtherCases) {
        return new MethodMatcher<>(LinkedHashMap.ofEntries(Stream.of(furtherCases).prepend(firstCase)));
    }

    static <A, B> Tuple2<Method, Handler<A, B>> methodCase(Method method,
                                                           Handler<A, B> handler) {
        return Tuple.of(method, handler);
    }

    static <A, B> PreProcessor<A, B> pipe(PreProcessor<A, B> preProcessor) {
        return preProcessor;
    }

    static <A, B> Handler<A, B> pipe(Handler<A, B> handler) {
        return handler;
    }
}