package fn4j.http.server;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class PreProcessors {
    private PreProcessors() {
    }

    public static PreProcessor<byte[], String> utf8() {
        return request -> request.mapBody(body -> new String(body, UTF_8));
    }
}