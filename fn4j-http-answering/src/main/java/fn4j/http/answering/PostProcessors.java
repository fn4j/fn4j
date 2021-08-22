package fn4j.http.answering;

import fn4j.http.core.header.HeaderValue;

import static fn4j.http.core.header.HeaderName.CONTENT_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;

public interface PostProcessors {
    static PostProcessor<String, byte[]> utf8() {
        return delegateResponse -> delegateResponse.mapBody(body -> body.getBytes(UTF_8))
                                                   .addHeader(CONTENT_TYPE, new HeaderValue("utf-8"));
    }
}