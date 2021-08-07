package fn4j.http.core;

import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Assertions;

public class AbstractResponseHeadAssert<SELF extends AbstractResponseHeadAssert<SELF, ACTUAL>, ACTUAL extends ResponseHead>
        extends AbstractObjectAssert<SELF, ACTUAL> {
    AbstractResponseHeadAssert(ACTUAL actual,
                               Class<?> selfType) {
        super(actual, selfType);
    }

    public SELF hasStatus(Status status) {
        Assertions.assertThat(actual.status()).isEqualTo(status);
        return myself;
    }

    public SELF hasStatusCode(StatusCode statusCode) {
        Assertions.assertThat(actual.status().statusCode()).isEqualTo(statusCode);
        return myself;
    }

    public SELF hasStatusCode(int statusCode) {
        return hasStatusCode(new StatusCode(statusCode));
    }
}