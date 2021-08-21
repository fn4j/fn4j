package fn4j.http.answering;

import fn4j.http.core.Request;
import fn4j.http.core.Response;
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

    static <A, B> PreProcessor<A, B> pipe(PreProcessor<A, B> preProcessor) {
        return preProcessor;
    }

    static <A, B> Handler<A, B> pipe(Handler<A, B> handler) {
        return handler;
    }

    static <A, B, C, D> Handler<A, D> wrap(PreProcessor<A, B> preProcessor,
                                           BiPostProcessor<A, C, D> biPostProcessor,
                                           Handler<B, C> handler) {
        return request -> handler.apply(preProcessor.apply(request))
                                 .map(response -> biPostProcessor.apply(request, response));
    }
}