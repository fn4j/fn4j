package fn4j.http.server.apachehc;

import fn4j.http.server.Handler;
import fn4j.http.server.ServerBuilder;

public class ApacheHcServerBuilder implements ServerBuilder {
    @Override
    public ApacheHcServer build(Handler<byte[], byte[]> handler) {
        return new ApacheHcServer(handler);
    }
}