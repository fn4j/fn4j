package fn4j.validation;

import net.jqwik.api.Example;

import static fn4j.validation.Validators.notEmpty;
import static fn4j.validation.Validators.notNull;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

class ExampleTest {
    @Example
    void should() {
        assertThat(notNull().apply(null).toEither()).isLeft();
        assertThat(notNull().apply("").toEither()).isRight();
        assertThat(notEmpty().apply("").toEither()).isLeft();
        assertThat(notEmpty().apply("-").toEither()).isRight();
    }
}