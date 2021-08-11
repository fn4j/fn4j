package fn4j.http.server.apachehc;

import fn4j.http.core.Request;
import fn4j.http.core.Response;
import fn4j.http.server.Server;
import io.vavr.concurrent.Future;
import io.vavr.concurrent.Promise;
import io.vavr.control.Try;
import org.apache.hc.core5.http.impl.bootstrap.AsyncServerBootstrap;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.function.Function;

import static org.apache.hc.core5.http.URIScheme.HTTP;
import static org.apache.hc.core5.io.CloseMode.GRACEFUL;

public class ApacheHcServer implements Server {
    public static final Logger LOG = LoggerFactory.getLogger(ApacheHcServer.class);

    private final Function<? super Request<byte[]>, ? extends Future<Response<byte[]>>> handler;

    public ApacheHcServer(Function<? super Request<byte[]>, ? extends Future<Response<byte[]>>> handler) {
        this.handler = handler;
    }

    @Override
    public Future<Closer> open(SocketAddress socketAddress) {
        var httpAsyncServer = AsyncServerBootstrap.bootstrap()
                                                  .setIOReactorConfig(IOReactorConfig.custom().setTcpNoDelay(true).build())
                                                  .register("*", new HandlerAsyncServerRequestHandler(handler))
                                                  .create();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> httpAsyncServer.close(GRACEFUL)));
        httpAsyncServer.start();
        var promise = Promise.<Void>make();
        new Thread(() -> {
            try {
                httpAsyncServer.awaitShutdown(TimeValue.MAX_VALUE);
                promise.complete(Try.success(null));
            } catch (InterruptedException e) {
                promise.failure(e);
            }
        }).start();
        return Future.fromJavaFuture(httpAsyncServer.listen(socketAddress, HTTP))
                     .map(__ -> new Closer() {
                         @Override
                         public void close() {
                             httpAsyncServer.close(GRACEFUL);
                         }

                         @Override
                         public Future<Void> awaitClose() {
                             return promise.future().await();
                         }
                     });
    }
}