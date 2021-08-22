package fn4j.http.core.header;

import io.vavr.Tuple;
import io.vavr.Tuple2;

public interface Header {
    HeaderName headerName();

    HeaderValue headerValue();

    default Tuple2<HeaderName, HeaderValue> tuple() {
        return Tuple.of(headerName(), headerValue());
    }
}