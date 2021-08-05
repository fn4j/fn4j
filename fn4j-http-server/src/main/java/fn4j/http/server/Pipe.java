package fn4j.http.server;

public final class Pipe {
    private Pipe() {
    }

    public static <A, B> PreProcessor<A, B> pipe(PreProcessor<A, B> preProcessor) {
        return preProcessor;
    }

    public static <A, B> Handler<A, B> pipe(Handler<A, B> handler) {
        return handler;
    }
}