package fn4j.http.core;

import io.vavr.control.Option;

import java.util.function.Function;

import static fn4j.http.core.Request.request;
import static fn4j.http.core.Response.response;

public interface Message<B> extends Head {
    Option<Body<B>> maybeBody();

    @Override
    Message<B> addHeader(HeaderName headerName,
                         HeaderValue headerValue);

    <C> Message<C> mapBody(Function<? super B, ? extends C> mapper);

    default Request<B> toRequest(Method method,
                                 Uri uri) {
        return request(method, uri, headers(), maybeBody());
    }

    default Response<B> toResponse(Status status) {
        return response(status, headers(), maybeBody());
    }

    static <B> Message<B> message(Headers headers,
                                  Option<Body<B>> maybeBody) {
        return new Immutable<>(headers, maybeBody);
    }

    record Immutable<B>(Headers headers,
                        Option<Body<B>> maybeBody) implements Message<B> {
        @Override
        public Message<B> addHeader(HeaderName headerName,
                                    HeaderValue headerValue) {
            return new Message.Immutable<>(headers.add(headerName, headerValue),
                                           maybeBody);
        }

        @Override
        public <C> Message<C> mapBody(Function<? super B, ? extends C> mapper) {
            return new Message.Immutable<>(headers,
                                           maybeBody.map(body -> new Body<>(mapper.apply(body.value()))));
        }
    }
}