package fn4j.http;

import io.vavr.control.Option;

import java.util.function.Function;

public interface Request<B> extends RequestHead, Message<B> {
    @Override
    Request<B> addHeader(HeaderName headerName,
                         HeaderValue headerValue);

    @Override
    <C> Request<C> mapBody(Function<? super B, ? extends C> mapper);

    static <B> Request<B> request(Method method,
                                  RequestUri requestUri,
                                  Headers headers,
                                  Option<Body<B>> maybeBody) {
        return new Immutable<>(method, requestUri, headers, maybeBody);
    }

    record Immutable<B>(Method method,
                        RequestUri requestUri,
                        Headers headers,
                        Option<Body<B>> maybeBody) implements Request<B> {
        @Override
        public Request<B> addHeader(HeaderName headerName,
                                    HeaderValue headerValue) {
            return new Request.Immutable<>(method,
                                           requestUri,
                                           headers.add(headerName, headerValue),
                                           maybeBody);
        }

        @Override
        public <C> Request<C> mapBody(Function<? super B, ? extends C> mapper) {
            return new Request.Immutable<>(method,
                                           requestUri,
                                           headers,
                                           maybeBody.map(body -> new Body<>(mapper.apply(body.value()))));
        }
    }
}