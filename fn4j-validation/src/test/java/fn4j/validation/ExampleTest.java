package fn4j.validation;

import io.vavr.collection.Stream;
import net.jqwik.api.Example;

import static fn4j.validation.Movement.name;
import static fn4j.validation.Validation.invalid;
import static fn4j.validation.Validation.valid;
import static fn4j.validation.Validators.*;
import static fn4j.validation.Violation.key;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

class ExampleTest {
    @Example
    void should() {
        assertThat(notNull().validate(null).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.movements()).isEmpty();
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.notNull"));
            });
        });
        assertThat(notNull().validate("").toEither()).hasRightValueSatisfying(valid -> {
            assertThat(valid.value()).isEqualTo("");
        });
        assertThat(Strings.notBlank().validate("").toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.movements()).isEmpty();
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
            });
        });
        assertThat(Strings.notBlank().validate(null).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.movements()).isEmpty();
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.notNull"));
            });
        });
        assertThat(Strings.notBlank().validate(" ").toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.movements()).isEmpty();
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
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
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Iterables.notEmpty"));
            });
        });
        assertThat(Iterables.notEmpty().validate(java.util.List.of("")).toEither()).hasRightValueSatisfying(valid -> {
            assertThat(valid.value()).isEqualTo(java.util.List.of(""));
        });
        assertThat(Iterables.notEmpty().validate(Stream.empty()).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.movements()).isEmpty();
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Iterables.notEmpty"));
            });
        });
        assertThat(Iterables.notEmpty().validate(Stream.of("")).toEither()).hasRightValueSatisfying(valid -> {
            assertThat(valid.value()).isEqualTo(Stream.of(""));
        });
        assertThat(Iterables.each(Strings.notBlank()).validate(Stream.of("a", "")).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.movements()).singleElement().satisfies(movement -> {
                    assertThat(movement.name()).isEqualTo(name("[1]"));
                });
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
            });
        });
        assertThat(Iterables.each(Strings.notBlank()).validate(Stream.of("", "")).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).hasSize(2).satisfies(violation -> {
                assertThat(violation.movements()).singleElement().satisfies(movement -> {
                    assertThat(movement.name()).isEqualTo(name("[0]"));
                });
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
            }, atIndex(0)).satisfies(violation -> {
                assertThat(violation.movements()).singleElement().satisfies(movement -> {
                    assertThat(movement.name()).isEqualTo(name("[1]"));
                });
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
            }, atIndex(1));
        });
        assertThat(Iterables.each(Strings.notBlank()).validate(Stream.of("a", "b")).toEither()).hasRightValueSatisfying(valid -> {
            assertThat(valid.value()).isEqualTo(Stream.of("a", "b"));
        });
        assertThat(SumType.VALIDATOR.validate(new SumType("", 4)).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.movements()).singleElement().satisfies(movement -> {
                    assertThat(movement.name()).isEqualTo(name("a"));
                });
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
            });
        });
        assertThat(SumType.VALIDATOR.validate(new SumType("a", 3)).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.movements()).singleElement().satisfies(movement -> {
                    assertThat(movement.name()).isEqualTo(name("b"));
                });
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Integers.greaterThanOrEqualTo"));
            });
        });
        assertThat(SumType.VALIDATOR.validate(new SumType("", 3)).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).hasSize(2).satisfies(violation -> {
                assertThat(violation.movements()).singleElement().satisfies(movement -> {
                    assertThat(movement.name()).isEqualTo(name("a"));
                });
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
            }, atIndex(0)).satisfies(violation -> {
                assertThat(violation.movements()).singleElement().satisfies(movement -> {
                    assertThat(movement.name()).isEqualTo(name("b"));
                });
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Integers.greaterThanOrEqualTo"));
            }, atIndex(1));
        });
        assertThat(SumType.VALIDATOR.validate(new SumType("a", 4)).toEither()).hasRightValueSatisfying(valid -> {
            assertThat(valid.value()).isEqualTo(new SumType("a", 4));
        });
    }

    static record SumType(String a,
                          int b) {
        private static final Validator<String, String> A_VALIDATOR = Strings.notBlank();
        private static final Validator<Integer, Integer> B_VALIDATOR = Integers.greaterThanOrEqualTo(4);

        static final Validator<SumType, SumType> VALIDATOR = cursor -> {

            var aValidation = A_VALIDATOR.move("a", SumType::a).validate(cursor.value());
            var bValidation = B_VALIDATOR.move("b", SumType::b).validate(cursor.value());

            return aValidation.fold(aInvalid -> bValidation.fold(bInvalid -> invalid(aInvalid.violations()
                                                                                             .appendAll(bInvalid.violations())),
                                                                 bValid -> invalid(aInvalid.violations())),
                                    aValid -> bValidation.fold(bInvalid -> invalid(bInvalid.violations()),
                                                               bValid -> valid(cursor.value())));
        };
    }
}