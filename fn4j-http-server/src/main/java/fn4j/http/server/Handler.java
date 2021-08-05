package fn4j.http.server;

import fn4j.http.Request;
import fn4j.http.Response;
import io.vavr.Function1;
import io.vavr.concurrent.Future;

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
}