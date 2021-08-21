package fn4j.http.answering;

import fn4j.http.core.Response;
import io.vavr.Function1;

import java.util.function.Function;

@FunctionalInterface
public interface PostProcessor<A, B> extends Function1<Response<A>, Response<B>> {
    Response<B> apply(Response<A> response);

    static <A, B> PostProcessor<A, B> of(Function<Response<A>, Response<B>> postProcessor) {
        return postProcessor::apply;
    }
}