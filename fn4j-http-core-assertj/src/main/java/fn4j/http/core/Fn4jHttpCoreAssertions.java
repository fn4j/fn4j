package fn4j.http.core;

public interface Fn4jHttpCoreAssertions {
    static ResponseHeadAssert assertThat(ResponseHead actual) {
        return new ResponseHeadAssert(actual);
    }

    static <B> ResponseAssert<B> assertThat(Response<B> actual) {
        return new ResponseAssert<>(actual);
    }
}