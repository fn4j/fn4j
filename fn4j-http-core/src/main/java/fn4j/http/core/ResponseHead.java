package fn4j.http.core;

import fn4j.http.core.header.HeaderName;
import fn4j.http.core.header.HeaderValue;
import fn4j.http.core.header.Headers;
import io.vavr.control.Option;

import static fn4j.http.core.Response.response;

public interface ResponseHead extends Head {
    Status status();

    @Override
    ResponseHead addHeader(HeaderName headerName,
                           HeaderValue headerValue);

    default <B> Response<B> toResponse(Option<Body<B>> maybeBody) {
        return response(status(), headers(), maybeBody);
    }

    default <B> Response<B> toResponse(Body<B> body) {
        return toResponse(Option.of(body));
    }

    default <B> Response<B> toResponseWithoutBody() {
        return toResponse(Option.none());
    }

    static ResponseHead responseHead(Status status,
                                     Headers headers) {
        return new Immutable(status, headers);
    }

    record Immutable(Status status,
                     Headers headers) implements ResponseHead {
        @Override
        public ResponseHead addHeader(HeaderName headerName,
                                      HeaderValue headerValue) {
            return new ResponseHead.Immutable(status,
                                              headers.add(headerName, headerValue));
        }
    }
}