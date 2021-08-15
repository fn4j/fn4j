package fn4j.http.server.apachehc;

import fn4j.http.core.Request;
import fn4j.http.core.Response;
import fn4j.http.server.ServerBuilder;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import org.apache.hc.core5.io.CloseMode;

import java.net.InetSocketAddress;
import java.util.function.Function;

public class ApacheHcServerBuilder implements ServerBuilder<ApacheHcServer, ApacheHcServerBuilder> {
    private InetSocketAddress inetSocketAddress;
    private Option<CloseMode> maybeShutdownAutoCloseMode = Option.none();
    private Function<? super Request<byte[]>, ? extends Future<Response<byte[]>>> handler;

    public ApacheHcServerBuilder inetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
        return this;
    }

    public ApacheHcServerBuilder shutdownAutoCloseMode(CloseMode shutdownAutoCloseMode) {
        return maybeShutdownAutoCloseMode(Option.of(shutdownAutoCloseMode));
    }

    public ApacheHcServerBuilder noShutdownAutoCloseMode() {
        return maybeShutdownAutoCloseMode(Option.none());
    }

    public ApacheHcServerBuilder maybeShutdownAutoCloseMode(Option<CloseMode> maybeShutdownAutoCloseMode) {
        this.maybeShutdownAutoCloseMode = maybeShutdownAutoCloseMode;
        return this;
    }

    @Override
    public ApacheHcServerBuilder handler(Function<? super Request<byte[]>, ? extends Future<Response<byte[]>>> handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public ApacheHcServer build() {
        return new ApacheHcServer(inetSocketAddress, maybeShutdownAutoCloseMode, handler);
    }
}