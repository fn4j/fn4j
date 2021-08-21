package fn4j.http.answering;

import static java.nio.charset.StandardCharsets.UTF_8;

public interface PreProcessors {
    static PreProcessor<byte[], String> utf8() {
        return request -> request.mapBody(body -> new String(body, UTF_8));
    }
}