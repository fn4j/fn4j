package fn4j.http.core;

import io.vavr.control.Option;

import static fn4j.http.core.Request.request;

public interface RequestHead extends Head {
    Method method();

    RequestUri requestUri();

    @Override
    RequestHead addHeader(HeaderName headerName,
                          HeaderValue headerValue);

    default <B> Request<B> toRequest(Option<Body<B>> maybeBody) {
        return request(method(), requestUri(), headers(), maybeBody);
    }

    default <B> Request<B> toRequest(Body<B> body) {
        return toRequest(Option.of(body));
    }

    default <B> Request<B> toRequestWithoutBody() {
        return toRequest(Option.none());
    }

    static RequestHead requestHead(Method method,
                                   RequestUri requestUri,
                                   Headers headers) {
        return new Immutable(method, requestUri, headers);
    }

    record Immutable(Method method,
                     RequestUri requestUri,
                     Headers headers) implements RequestHead {
        @Override
        public RequestHead addHeader(HeaderName headerName,
                                     HeaderValue headerValue) {
            return new RequestHead.Immutable(method,
                                             requestUri,
                                             headers.add(headerName, headerValue));
        }
    }
}