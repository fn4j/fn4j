package fn4j.http.routing;

import fn4j.http.core.Request;
import fn4j.http.core.Response;
import io.vavr.Function2;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BiPostProcessor<A, B, C> extends Function2<Request<A>, Response<B>, Response<C>> {
    static <A, B, C> BiPostProcessor<A, B, C> of(BiFunction<? super Request<A>, ? super Response<B>, ? extends Response<C>> biPostProcessor) {
        return biPostProcessor::apply;
    }
}