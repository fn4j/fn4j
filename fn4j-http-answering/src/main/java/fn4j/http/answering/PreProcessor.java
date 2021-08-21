package fn4j.http.answering;

import fn4j.http.core.Request;
import io.vavr.Function1;

import java.util.function.Function;

@FunctionalInterface
public interface PreProcessor<A, B> extends Function1<Request<A>, Request<B>> {
    Request<B> apply(Request<A> request);

    default <C> PreProcessor<A, C> pipe(PreProcessor<B, C> preProcessor) {
        return of(preProcessor.compose(this));
    }

    default <C> Handler<A, C> pipe(Handler<B, C> handler) {
        return Handler.of(handler.compose(this));
    }

    static <A, B> PreProcessor<A, B> of(Function<? super Request<A>, ? extends Request<B>> preProcessor) {
        return preProcessor::apply;
    }
}