package fn4j.http.core.header;

import io.vavr.CheckedFunction1;
import io.vavr.Function1;
import io.vavr.control.Option;

import java.util.function.Function;

public interface HeaderReader<A> extends Function1<HeaderValue, Option<A>> {
    HeaderName headerName();

    Option<A> apply(HeaderValue headerValue);

    default <B> HeaderReader<B> map(Function<? super A, ? extends B> mapper) {
        return HeaderReader.of(headerName(), headerValue -> apply(headerValue).map(mapper));
    }

    default <B> HeaderReader<B> mapOption(Function<? super A, ? extends Option<? extends B>> mapper) {
        return flatMap(a -> HeaderReader.of(headerName(),
                                            headerValue -> apply(headerValue).flatMap(mapper)));
    }

    default <B> HeaderReader<B> mapTry(CheckedFunction1<? super A, ? extends B> mapper) {
        return mapOption(CheckedFunction1.lift(mapper));
    }

    default <B> HeaderReader<B> flatMap(Function<? super A, ? extends HeaderReader<B>> mapper) {
        return HeaderReader.of(headerName(), headerValue -> apply(headerValue).flatMap(a -> mapper.apply(a).apply(headerValue)));
    }

    static HeaderReader<HeaderValue> of(HeaderName headerName) {
        return of(headerName, Option::of);
    }

    static <H> HeaderReader<H> of(HeaderName headerName,
                                  Function<HeaderValue, ? extends Option<H>> apply) {
        return new FunctionHeaderReader<>(headerName, apply);
    }

    record FunctionHeaderReader<H>(HeaderName headerName,
                                   Function<HeaderValue, ? extends Option<H>> apply) implements HeaderReader<H> {
        @Override
        public Option<H> apply(HeaderValue headerValue) {
            return apply.apply(headerValue);
        }
    }
}