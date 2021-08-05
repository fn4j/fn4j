package fn4j.http;

import io.vavr.control.Option;

import static fn4j.http.Response.response;

public interface ResponseHead extends Head {
    StatusCode statusCode();

    @Override
    ResponseHead addHeader(HeaderName headerName,
                           HeaderValue headerValue);

    default <B> Response<B> toResponse(Option<Body<B>> maybeBody) {
        return response(statusCode(), headers(), maybeBody);
    }

    default <B> Response<B> toResponse(Body<B> body) {
        return toResponse(Option.of(body));
    }

    default <B> Response<B> toResponseWithoutBody() {
        return toResponse(Option.none());
    }

    static ResponseHead responseHead(StatusCode statusCode,
                                     Headers headers) {
        return new Immutable(statusCode, headers);
    }

    record Immutable(StatusCode statusCode,
                     Headers headers) implements ResponseHead {
        @Override
        public ResponseHead addHeader(HeaderName headerName,
                                      HeaderValue headerValue) {
            return new ResponseHead.Immutable(statusCode,
                                              headers.add(headerName, headerValue));
        }
    }
}