package fn4j.http.core;

import org.assertj.core.api.Assertions;
import org.assertj.vavr.api.VavrAssertions;

import java.util.function.Consumer;

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

    public SELF hasBody(B bodyValue) {
        VavrAssertions.assertThat(actual.maybeBody()).contains(new Body<>(bodyValue));
        return myself;
    }

    public SELF hasBodySatisfying(Consumer<B> bodyValueRequirements) {
        VavrAssertions.assertThat(actual.maybeBody())
                      .hasValueSatisfying(body -> Assertions.assertThat(body.value())
                                                            .satisfies(bodyValueRequirements));
        return myself;
    }
}