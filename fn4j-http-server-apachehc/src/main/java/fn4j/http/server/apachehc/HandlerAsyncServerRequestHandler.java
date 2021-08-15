package fn4j.http.server.apachehc;

import fn4j.http.core.Headers;
import fn4j.http.core.Request;
import fn4j.http.core.Response;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.nio.AsyncRequestConsumer;
import org.apache.hc.core5.http.nio.AsyncServerRequestHandler;
import org.apache.hc.core5.http.nio.entity.BasicAsyncEntityConsumer;
import org.apache.hc.core5.http.nio.support.BasicRequestConsumer;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.IOException;
import java.util.function.Function;

import static fn4j.http.core.Body.maybeBody;
import static fn4j.http.core.Status.BAD_REQUEST;
import static fn4j.http.core.Status.INTERNAL_SERVER_ERROR;
import static fn4j.http.server.apachehc.ApacheHcServer.LOG;
import static fn4j.http.server.apachehc.Conversions.ApacheHc.asyncResponseProducer;
import static fn4j.http.server.apachehc.Conversions.requestHead;

public class HandlerAsyncServerRequestHandler implements AsyncServerRequestHandler<Message<HttpRequest, byte[]>> {
    private final Function<? super Request<byte[]>, ? extends Future<Response<byte[]>>> handler;

    public HandlerAsyncServerRequestHandler(Function<? super Request<byte[]>, ? extends Future<Response<byte[]>>> handler) {
        this.handler = handler;
    }

    @Override
    public AsyncRequestConsumer<Message<HttpRequest, byte[]>> prepare(HttpRequest request,
                                                                      EntityDetails entityDetails,
                                                                      HttpContext context) {
        return new BasicRequestConsumer<>(new BasicAsyncEntityConsumer());
    }

    @Override
    public void handle(Message<HttpRequest, byte[]> message,
                       ResponseTrigger responseTrigger,
                       HttpContext context) {
        requestHead(message.getHead())
                .<Future<Response<byte[]>>>fold(
                        uriError -> {
                            LOG.debug(uriError.getLocalizedMessage(), uriError);
                            return Future.successful(new Response.Immutable<>(BAD_REQUEST, Headers.empty(), Option.none()));
                        },
                        requestHead -> {
                            var request = requestHead.toRequest(maybeBody(message.getBody()));
                            return handler.apply(request);
                        }
                )
                .onComplete(maybeResponse -> {
                    var response = maybeResponse.getOrElseGet(error -> {
                        LOG.error(error.getLocalizedMessage(), error);
                        return new Response.Immutable<>(INTERNAL_SERVER_ERROR, Headers.empty(), Option.none());
                    });

                    try {
                        responseTrigger.submitResponse(asyncResponseProducer(response, context.getProtocolVersion()), context);
                    } catch (HttpException | IOException error) {
                        LOG.error(error.getLocalizedMessage(), error);
                    }
                });
    }
}