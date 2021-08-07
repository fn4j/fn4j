package fn4j.http.server;

import fn4j.http.core.HeaderValue;

import static fn4j.http.core.HeaderName.CONTENT_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class PostProcessors {
    private PostProcessors() {
    }

    public static PostProcessor<String, byte[]> utf8() {
        return delegateResponse -> delegateResponse.mapBody(body -> body.getBytes(UTF_8))
                                                   .addHeader(CONTENT_TYPE, new HeaderValue("utf-8"));
    }
}