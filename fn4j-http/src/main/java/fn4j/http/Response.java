package fn4j.http;

import io.vavr.control.Option;

import java.util.function.Function;

public interface Response<B> extends ResponseHead, Message<B> {
    @Override
    Response<B> addHeader(HeaderName headerName,
                          HeaderValue headerValue);

    @Override
    <C> Response<C> mapBody(Function<? super B, ? extends C> mapper);

    static <B> Response<B> response(StatusCode statusCode,
                                    Headers headers,
                                    Option<Body<B>> maybeBody) {
        return new Immutable<>(statusCode, headers, maybeBody);
    }

    record Immutable<B>(StatusCode statusCode,
                        Headers headers,
                        Option<Body<B>> maybeBody) implements Response<B> {
        @Override
        public Response<B> addHeader(HeaderName headerName,
                                     HeaderValue headerValue) {
            return new Response.Immutable<>(statusCode,
                                            headers.add(headerName, headerValue),
                                            maybeBody);
        }

        @Override
        public <C> Response<C> mapBody(Function<? super B, ? extends C> mapper) {
            return new Response.Immutable<>(statusCode,
                                            headers,
                                            maybeBody.map(body -> new Body<>(mapper.apply(body.value()))));
        }
    }
}