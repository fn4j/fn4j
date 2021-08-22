package fn4j.http.core;

import fn4j.http.core.header.HeaderName;
import fn4j.http.core.header.HeaderValue;
import fn4j.http.core.header.Headers;
import fn4j.net.uri.Uri;
import io.vavr.control.Option;

import java.util.function.Function;

public interface Request<B> extends RequestHead, Message<B> {
    @Override
    Request<B> addHeader(HeaderName headerName,
                         HeaderValue headerValue);

    @Override
    <C> Request<C> mapBody(Function<? super B, ? extends C> mapper);

    static <B> Request<B> request(Method method,
                                  Uri uri,
                                  Headers headers,
                                  Option<Body<B>> maybeBody) {
        return new Immutable<>(method, uri, headers, maybeBody);
    }

    static <B> Request<B> request(Method method,
                                  Uri uri,
                                  Headers headers,
                                  Body<B> body) {
        return request(method, uri, headers, Option.of(body));
    }

    static <B> Request<B> request(Method method,
                                  Uri uri,
                                  Headers headers) {
        return request(method, uri, headers, Option.none());
    }

    record Immutable<B>(Method method,
                        Uri uri,
                        Headers headers,
                        Option<Body<B>> maybeBody) implements Request<B> {
        @Override
        public Request<B> addHeader(HeaderName headerName,
                                    HeaderValue headerValue) {
            return new Request.Immutable<>(method,
                                           uri,
                                           headers.add(headerName, headerValue),
                                           maybeBody);
        }

        @Override
        public <C> Request<C> mapBody(Function<? super B, ? extends C> mapper) {
            return new Request.Immutable<>(method,
                                           uri,
                                           headers,
                                           maybeBody.map(body -> new Body<>(mapper.apply(body.value()))));
        }
    }
}