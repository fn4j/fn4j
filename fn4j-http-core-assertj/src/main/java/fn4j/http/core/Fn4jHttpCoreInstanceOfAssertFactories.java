package fn4j.http.core;

import org.assertj.core.api.InstanceOfAssertFactory;

public interface Fn4jHttpCoreInstanceOfAssertFactories {
    @SuppressWarnings("rawtypes")
    InstanceOfAssertFactory<Response, ResponseAssert<Object>> RESPONSE = response(Object.class);

    @SuppressWarnings("rawtypes")
    static <B> InstanceOfAssertFactory<Response, ResponseAssert<B>> response(Class<B> bodyClass) {
        return new InstanceOfAssertFactory<>(Response.class, Fn4jHttpCoreAssertions::<B>assertThat);
    }

    static InstanceOfAssertFactory<ResponseHead, ResponseHeadAssert> responseHead() {
        return new InstanceOfAssertFactory<>(ResponseHead.class, Fn4jHttpCoreAssertions::assertThat);
    }
}