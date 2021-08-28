package fn4j.validation;

import io.vavr.collection.Stream;
import net.jqwik.api.Example;

import static fn4j.validation.Validation.invalid;
import static fn4j.validation.Validation.valid;
import static fn4j.validation.Validators.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

class ExampleTest {
    @Example
    void should() {
        assertThat(notNull().validate(null).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.movements()).isEmpty();
                assertThat(violation.key()).isEqualTo(new Violation.Key("fn4j.validation.Validators.notNull"));
            });
        });
        assertThat(notNull().validate("").toEither()).hasRightValueSatisfying(valid -> {
            assertThat(valid.value()).isEqualTo("");
        });
        assertThat(Strings.notBlank().validate("").toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.movements()).isEmpty();
                assertThat(violation.key()).isEqualTo(new Violation.Key("fn4j.validation.Validators.Strings.notBlank"));
            });
        });
        assertThat(Strings.notBlank().validate(" ").toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.movements()).isEmpty();
                assertThat(violation.key()).isEqualTo(new Violation.Key("fn4j.validation.Validators.Strings.notBlank"));
            });
        });
        assertThat(Strings.notBlank().validate("a").toEither()).hasRightValueSatisfying(valid -> {
            assertThat(valid.value()).isEqualTo("a");
        });
        assertThat(Strings.notBlank().validate(" a").toEither()).hasRightValueSatisfying(valid -> {
            assertThat(valid.value()).isEqualTo(" a");
        });
        assertThat(Iterables.notEmpty().validate(java.util.List.of()).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.movements()).isEmpty();
                assertThat(violation.key()).isEqualTo(new Violation.Key("fn4j.validation.Validators.Iterables.notEmpty"));
            });
        });
        assertThat(Iterables.notEmpty().validate(java.util.List.of("")).toEither()).isRight();
        assertThat(Iterables.notEmpty().validate(io.vavr.collection.Stream.empty()).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.movements()).isEmpty();
                assertThat(violation.key()).isEqualTo(new Violation.Key("fn4j.validation.Validators.Iterables.notEmpty"));
            });
        });
        assertThat(Iterables.notEmpty().validate(io.vavr.collection.Stream.of("")).toEither()).isRight();
        assertThat(Iterables.each(Strings.notBlank()).validate(Stream.of("a", "")).toEither()).isLeft();
        assertThat(Iterables.each(Strings.notBlank()).validate(Stream.of("a", "b")).toEither()).isRight();
        assertThat(SumType.VALIDATOR.validate(new SumType("", 4)).toEither()).isLeft();
        assertThat(SumType.VALIDATOR.validate(new SumType("a", 3)).toEither()).isLeft();
        assertThat(SumType.VALIDATOR.validate(new SumType("", 3)).toEither()).isLeft();
        assertThat(SumType.VALIDATOR.validate(new SumType("a", 4)).toEither()).isRight();
    }

    static record SumType(String a,
                          int b) {
        private static final Validator<String, String> A_VALIDATOR = Strings.notBlank();
        private static final Validator<Integer, Integer> B_VALIDATOR = Integers.greaterThanOrEqualTo(4);

        static final Validator<SumType, SumType> VALIDATOR = cursor -> {
            var aValidation = A_VALIDATOR.validate(cursor.value().a());
            var bValidation = B_VALIDATOR.validate(cursor.value().b());
            return aValidation.fold(aInvalid -> bValidation.fold(bInvalid -> invalid(aInvalid.violations()
                                                                                             .appendAll(bInvalid.violations())),
                                                                 bValid -> invalid(aInvalid.violations())),
                                    aValid -> bValidation.fold(bInvalid -> invalid(bInvalid.violations()),
                                                               bValid -> valid(cursor.value())));
        };
    }
}