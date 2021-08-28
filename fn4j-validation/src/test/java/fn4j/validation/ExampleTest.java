package fn4j.validation;

import io.vavr.collection.Stream;
import net.jqwik.api.Example;

import static fn4j.validation.Validation.invalid;
import static fn4j.validation.Validation.valid;
import static fn4j.validation.Validators.*;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

class ExampleTest {
    @Example
    void should() {
        assertThat(notNull().apply(null).toEither()).isLeft();
        assertThat(notNull().apply("").toEither()).isRight();
        assertThat(Strings.notBlank().apply("").toEither()).isLeft();
        assertThat(Strings.notBlank().apply(" ").toEither()).isLeft();
        assertThat(Strings.notBlank().apply("-").toEither()).isRight();
        assertThat(Strings.notBlank().apply(" -").toEither()).isRight();
        assertThat(Iterables.notEmpty().apply(java.util.List.of()).toEither()).isLeft();
        assertThat(Iterables.notEmpty().apply(java.util.List.of("")).toEither()).isRight();
        assertThat(Iterables.notEmpty().apply(io.vavr.collection.Stream.empty()).toEither()).isLeft();
        assertThat(Iterables.notEmpty().apply(io.vavr.collection.Stream.of("")).toEither()).isRight();
        assertThat(Iterables.each(Strings.notBlank()).apply(Stream.of("a", "")).toEither()).isLeft();
        assertThat(Iterables.each(Strings.notBlank()).apply(Stream.of("a", "b")).toEither()).isRight();
        assertThat(SumType.VALIDATOR.apply(new SumType("", 4)).toEither()).isLeft();
        assertThat(SumType.VALIDATOR.apply(new SumType("a", 3)).toEither()).isLeft();
        assertThat(SumType.VALIDATOR.apply(new SumType("", 3)).toEither()).isLeft();
        assertThat(SumType.VALIDATOR.apply(new SumType("a", 4)).toEither()).isRight();
    }

    static record SumType(String a,
                          int b) {
        private static final Validator<String, String> A_VALIDATOR = Strings.notBlank();
        private static final Validator<Integer, Integer> B_VALIDATOR = Integers.greaterThanOrEqualTo(4);

        static final Validator<SumType, SumType> VALIDATOR = value -> {
            var aValidation = A_VALIDATOR.apply(value.a());
            var bValidation = B_VALIDATOR.apply(value.b());
            return aValidation.fold(aInvalid -> bValidation.fold(bInvalid -> invalid(aInvalid.violations()
                                                                                             .appendAll(bInvalid.violations())),
                                                                 bValid -> invalid(aInvalid.violations())),
                                    aValid -> bValidation.fold(bInvalid -> invalid(bInvalid.violations()),
                                                               bValid -> valid(value)));
        };
    }
}