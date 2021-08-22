package fn4j.http.core.header;

import io.vavr.Tuple2;

public record RawHeader(HeaderName headerName,
                        HeaderValue headerValue) implements Header {
    public RawHeader(Tuple2<HeaderName, HeaderValue> header) {
        this(header._1(), header._2());
    }
}