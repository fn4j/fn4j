package fn4j.http.core;

import fn4j.http.core.header.HeaderName;
import fn4j.http.core.header.HeaderValue;
import fn4j.http.core.header.Headers;
import fn4j.net.uri.Uri;
import io.vavr.control.Option;

import static fn4j.http.core.Request.request;

public interface RequestHead extends Head {
    Method method();

    Uri uri();

    @Override
    RequestHead addHeader(HeaderName headerName,
                          HeaderValue headerValue);

    default <B> Request<B> toRequest(Option<Body<B>> maybeBody) {
        return request(method(), uri(), headers(), maybeBody);
    }

    default <B> Request<B> toRequest(Body<B> body) {
        return toRequest(Option.of(body));
    }

    default <B> Request<B> toRequestWithoutBody() {
        return toRequest(Option.none());
    }

    static RequestHead requestHead(Method method,
                                   Uri uri,
                                   Headers headers) {
        return new Immutable(method, uri, headers);
    }

    record Immutable(Method method,
                     Uri uri,
                     Headers headers) implements RequestHead {
        @Override
        public RequestHead addHeader(HeaderName headerName,
                                     HeaderValue headerValue) {
            return new RequestHead.Immutable(method,
                                             uri,
                                             headers.add(headerName, headerValue));
        }
    }
}