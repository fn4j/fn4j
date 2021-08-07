package fn4j.http.core;

import io.vavr.control.Option;

import java.util.function.Function;

public interface Response<B> extends ResponseHead, Message<B> {
    @Override
    Response<B> addHeader(HeaderName headerName,
                          HeaderValue headerValue);

    @Override
    <C> Response<C> mapBody(Function<? super B, ? extends C> mapper);

    static <B> Response<B> response(Status status,
                                    Headers headers,
                                    Option<Body<B>> maybeBody) {
        return new Immutable<>(status, headers, maybeBody);
    }

    record Immutable<B>(Status status,
                        Headers headers,
                        Option<Body<B>> maybeBody) implements Response<B> {
        @Override
        public Response<B> addHeader(HeaderName headerName,
                                     HeaderValue headerValue) {
            return new Response.Immutable<>(status,
                                            headers.add(headerName, headerValue),
                                            maybeBody);
        }

        @Override
        public <C> Response<C> mapBody(Function<? super B, ? extends C> mapper) {
            return new Response.Immutable<>(status,
                                            headers,
                                            maybeBody.map(body -> new Body<>(mapper.apply(body.value()))));
        }
    }
}