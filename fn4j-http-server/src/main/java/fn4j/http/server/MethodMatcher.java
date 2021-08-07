package fn4j.http.server;

import fn4j.http.core.HeaderValue;
import fn4j.http.core.Method;
import fn4j.http.core.Request;
import fn4j.http.core.Response;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;

import static fn4j.http.core.HeaderName.ALLOW;
import static fn4j.http.core.Headers.headers;
import static fn4j.http.core.Method.GET;
import static fn4j.http.core.Method.HEAD;
import static fn4j.http.core.ResponseHead.responseHead;
import static fn4j.http.core.Status.METHOD_NOT_ALLOWED;
import static fn4j.http.server.Handler.methodCase;

public class MethodMatcher<A, B> implements PartialHandler<A, B> {
    private final LinkedHashMap<Method, Handler<A, B>> cases;

    public MethodMatcher(LinkedHashMap<Method, Handler<A, B>> cases) {
        this.cases = cases;
    }

    @Override
    public Option<Future<Response<B>>> apply(Request<A> request) {
        return cases.get(request.method()).map(handler -> handler.apply(request));
    }

    public Handler<A, B> orMethodNotAllowed() {
        return orElse(methodNotAllowed());
    }

    public static <A, B> Tuple2<Method, Handler<A, B>> GET(Handler<A, B> handler) {
        return methodCase(GET, handler);
    }

    public static <A, B> Tuple2<Method, Handler<A, B>> HEAD(Handler<A, B> handler) {
        return methodCase(HEAD, handler);
    }

    // TODO: POST, ...

    private Handler<A, B> methodNotAllowed() {
        return request -> {
            var allowHeaderValue = new HeaderValue(cases.keysIterator().map(Method::value).mkString(","));
            var allowHeader = Tuple.of(ALLOW, allowHeaderValue);
            var response = responseHead(METHOD_NOT_ALLOWED, headers(allowHeader)).<B>toResponseWithoutBody();
            return Future.successful(response);
        };
    }
}