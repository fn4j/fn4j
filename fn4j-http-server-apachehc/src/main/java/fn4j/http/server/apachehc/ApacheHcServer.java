package fn4j.http.server.apachehc;

import fn4j.http.core.Request;
import fn4j.http.core.Response;
import fn4j.http.server.Server;
import fn4j.http.shared.Closer;
import io.vavr.concurrent.Future;
import io.vavr.concurrent.Promise;
import io.vavr.control.Option;
import org.apache.hc.core5.http.impl.bootstrap.AsyncServerBootstrap;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.function.Function;

import static java.lang.Runtime.getRuntime;
import static org.apache.hc.core5.http.URIScheme.HTTP;
import static org.apache.hc.core5.io.CloseMode.GRACEFUL;

public class ApacheHcServer implements Server {
    static final Logger LOG = LoggerFactory.getLogger(ApacheHcServer.class);

    private final InetSocketAddress inetSocketAddress;
    private final Option<CloseMode> maybeShutdownAutoCloseMode;
    private final Function<? super Request<byte[]>, ? extends Future<Response<byte[]>>> handler;

    public ApacheHcServer(InetSocketAddress inetSocketAddress,
                          Option<CloseMode> maybeShutdownAutoCloseMode,
                          Function<? super Request<byte[]>, ? extends Future<Response<byte[]>>> handler) {
        this.inetSocketAddress = inetSocketAddress;
        this.maybeShutdownAutoCloseMode = maybeShutdownAutoCloseMode;
        this.handler = handler;
    }

    public static ApacheHcServerBuilder builder() {
        return new ApacheHcServerBuilder();
    }

    @Override
    public Future<Closer> open() {
        var httpAsyncServer = AsyncServerBootstrap.bootstrap()
                                                  .setIOReactorConfig(IOReactorConfig.custom().setTcpNoDelay(true).build())
                                                  .register("*", new HandlerAsyncServerRequestHandler(handler))
                                                  .create();
        maybeShutdownAutoCloseMode.forEach(shutdownAutoCloseMode -> getRuntime().addShutdownHook(new Thread(() -> httpAsyncServer.close(shutdownAutoCloseMode))));
        httpAsyncServer.start();

        var promise = Promise.<Void>make().completeWith(Future.of(() -> {
            httpAsyncServer.awaitShutdown(TimeValue.MAX_VALUE);
            return null;
        }));

        return Future.fromJavaFuture(httpAsyncServer.listen(inetSocketAddress, HTTP))
                     .map(__ -> new Closer() {
                         @Override
                         public void close() {
                             httpAsyncServer.close(GRACEFUL);
                         }

                         @Override
                         public Future<Void> onClose() {
                             return promise.future();
                         }
                     });
    }
}