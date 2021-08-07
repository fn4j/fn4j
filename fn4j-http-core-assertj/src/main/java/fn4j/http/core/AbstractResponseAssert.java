package fn4j.http.core;

import org.assertj.vavr.api.VavrAssertions;

public class AbstractResponseAssert<SELF extends AbstractResponseAssert<SELF, ACTUAL, B>, ACTUAL extends Response<B>, B>
        extends AbstractResponseHeadAssert<SELF, ACTUAL> {
    AbstractResponseAssert(ACTUAL actual,
                           Class<?> selfType) {
        super(actual, selfType);
    }

    public SELF hasNoBody() {
        VavrAssertions.assertThat(actual.maybeBody()).isEmpty();
        return myself;
    }
}