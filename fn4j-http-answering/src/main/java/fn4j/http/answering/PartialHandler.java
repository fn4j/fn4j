package fn4j.http.answering;

import fn4j.http.core.Request;
import fn4j.http.core.Response;
import io.vavr.Function1;
import io.vavr.collection.Stream;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;

import java.util.function.Function;

@FunctionalInterface
public interface PartialHandler<A, B> extends Function1<Request<A>, Option<Future<Response<B>>>> {
    @Override
    Option<Future<Response<B>>> apply(Request<A> request);

    default boolean isDefinedFor(Request<A> request) {
        return apply(request).isDefined();
    }

    default Handler<A, B> orElse(Handler<A, B> other) {
        return request -> apply(request).getOrElse(() -> other.apply(request));
    }

    static <A, B> PartialHandler<A, B> of(Function<? super Request<A>, ? extends Option<Future<Response<B>>>> partialHandler) {
        return partialHandler::apply;
    }

    static <A, B> PartialHandler<A, B> of(Option<? extends Handler<A, B>> maybeHandler) {
        return request -> maybeHandler.map(handler -> handler.apply(request));
    }

    @SafeVarargs
    static <A, B> PartialHandler<A, B> ofAll(PartialHandler<A, B>... partialHandlers) {
        return ofAll(Stream.of(partialHandlers));
    }

    static <A, B> PartialHandler<A, B> ofAll(Iterable<? extends PartialHandler<A, B>> partialHandlers) {
        return request -> Stream.ofAll(partialHandlers)
                                .flatMap(partialHandler -> partialHandler.apply(request))
                                .headOption();
    }
}