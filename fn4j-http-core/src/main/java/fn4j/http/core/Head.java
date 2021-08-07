package fn4j.http.core;

import io.vavr.control.Option;

import static fn4j.http.core.Message.message;
import static fn4j.http.core.RequestHead.requestHead;
import static fn4j.http.core.ResponseHead.responseHead;

public interface Head {
    Headers headers();

    Head addHeader(HeaderName headerName,
                   HeaderValue headerValue);

    default RequestHead toRequestHead(Method method,
                                      Uri uri) {
        return requestHead(method, uri, headers());
    }

    default ResponseHead toResponseHead(Status status) {
        return responseHead(status, headers());
    }

    default <B> Message<B> toMessage(Option<Body<B>> maybeBody) {
        return message(headers(), maybeBody);
    }

    static Head head(Headers headers) {
        return new Immutable(headers);
    }

    record Immutable(Headers headers) implements Head {
        @Override
        public Head addHeader(HeaderName headerName,
                              HeaderValue headerValue) {
            return new Immutable(headers.add(headerName, headerValue));
        }
    }
}