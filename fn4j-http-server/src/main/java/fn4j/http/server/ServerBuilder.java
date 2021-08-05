package fn4j.http.server;

@FunctionalInterface
public interface ServerBuilder {
    Server build(Handler<byte[], byte[]> handler);
}