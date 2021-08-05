package fn4j.http;

import io.vavr.control.Option;

import static fn4j.http.Message.message;
import static fn4j.http.RequestHead.requestHead;
import static fn4j.http.ResponseHead.responseHead;

public interface Head {
    Headers headers();

    Head addHeader(HeaderName headerName,
                   HeaderValue headerValue);

    default fn4j.http.RequestHead toRequestHead(Method method,
                                                RequestUri requestUri) {
        return requestHead(method, requestUri, headers());
    }

    default fn4j.http.ResponseHead toResponseHead(StatusCode statusCode) {
        return responseHead(statusCode, headers());
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