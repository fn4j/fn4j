package fn4j.http.core;

import fn4j.http.core.header.HeaderName;
import fn4j.http.core.header.HeaderValue;
import fn4j.http.core.header.Headers;
import org.assertj.vavr.api.MultimapAssert;
import org.assertj.vavr.api.VavrAssertions;

public interface Fn4jHttpCoreAssertions {
    static MultimapAssert<HeaderName, HeaderValue> assertThat(Headers headers) {
        return VavrAssertions.assertThat(headers.multimap());
    }

    static ResponseHeadAssert assertThat(ResponseHead actual) {
        return new ResponseHeadAssert(actual);
    }

    static <B> ResponseAssert<B> assertThat(Response<B> actual) {
        return new ResponseAssert<>(actual);
    }
}