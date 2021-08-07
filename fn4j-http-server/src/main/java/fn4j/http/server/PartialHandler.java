package fn4j.http.server;

import fn4j.http.core.Request;
import fn4j.http.core.Response;
import io.vavr.Function1;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;

@FunctionalInterface
public interface PartialHandler<A, B> extends Function1<Request<A>, Option<Future<Response<B>>>> {
    @Override
    Option<Future<Response<B>>> apply(Request<A> aRequest);

    default Handler<A, B> orElse(Handler<A, B> other) {
        return request -> apply(request).getOrElse(other.apply(request));
    }
}