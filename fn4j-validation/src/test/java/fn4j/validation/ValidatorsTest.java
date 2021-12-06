package fn4j.validation;

import io.vavr.control.Try;
import net.jqwik.api.*;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.CharRange;
import net.jqwik.api.constraints.NotBlank;
import net.jqwik.api.constraints.NotEmpty;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static fn4j.validation.ValidationResult.invalid;
import static fn4j.validation.Violation.key;
import static fn4j.validation.Violation.violation;
import static net.jqwik.api.Assume.that;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

@Label("Validators")
class ValidatorsTest {

    @Group
    @Label("notNull")
    class NotNullTest {

        @Property
        @Label("should be valid if not null")
        void shouldBeValidIfNotNull(@ForAll Object object) {
            // when
            ValidationResult<Object> result = Validators.notNull().apply(object);

            // then
            assertThat(result.toValuesEither()).containsRightSame(object);
        }

        @Example
        @Label("should be invalid if null")
        void shouldBeInvalidIfNull() {
            // when
            ValidationResult<Object> result = Validators.notNull().apply(null);

            // then
            assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                assertThat(violations).singleElement().satisfies(violation -> {
                    assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.notNull"));
                    assertThat(violation.path()).isEmpty();
                });
            });
        }
    }

    @Group
    @Label("Iterables")
    class IterablesTest {

        @Group
        @Label("notEmpty")
        class IterablesNotEmptyTest {

            @Property
            @Label("should be valid if not empty")
            void shouldBeValidIfNotEmpty(@ForAll @NotEmpty Iterable<?> iterable) {
                // when
                ValidationResult<Iterable<?>> result = Validators.Iterables.notEmpty().apply(iterable);

                // then
                assertThat(result.toValuesEither()).containsRightSame(iterable);
            }

            @Example
            @Label("should be invalid if null")
            void shouldBeInvalidIfNull() {
                // when
                ValidationResult<Iterable<?>> result = Validators.Iterables.notEmpty().apply(null);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.notNull"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }

            @Example
            @Label("should be invalid if empty")
            <A> void shouldBeInvalidIfEmpty() {
                // given
                Iterable<A> emptyIterable = emptyIterable();

                // when
                ValidationResult<Iterable<?>> result = Validators.Iterables.notEmpty().apply(emptyIterable);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Iterables.notEmpty"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }
        }

        @Group
        @Label("each")
        class IterablesEachTest {

            @Example
            @Label("should be valid if empty")
            <A> void shouldBeValidIfEmpty() {
                // given
                Iterable<A> iterable = emptyIterable();
                Validator<A, A> alwaysInvalidValidator = alwaysInvalidValidator();

                // when
                ValidationResult<Iterable<A>> result = Validators.Iterables.each(alwaysInvalidValidator).apply(iterable);

                // then
                assertThat(result.toValuesEither()).containsRightSame(iterable);
            }

            // TODO: should be valid if all elements are valid

            @Example
            @Label("should be invalid if null")
            <A> void shouldBeInvalidIfNull() {
                // given
                Validator<A, A> alwaysValidValidator = alwaysValidValidator();

                // when
                ValidationResult<Iterable<A>> result = Validators.Iterables.each(alwaysValidValidator).apply(null);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.notNull"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }

            // TODO: should be invalid if one ore more elements are invalid
        }
    }

    @Group
    @Label("Strings")
    class StringsTest {

        @Group
        @Label("notEmpty")
        class StringsNotEmptyTest {

            @Property
            @Label("should be valid if not empty")
            void shouldBeValidIfNotEmpty(@ForAll @NotEmpty String string) {
                // when
                ValidationResult<String> result = Validators.Strings.notEmpty().apply(string);

                // then
                assertThat(result.toValuesEither()).containsRightSame(string);
            }

            @Example
            @Label("should be invalid if null")
            void shouldBeInvalidIfNull() {
                // when
                ValidationResult<String> result = Validators.Strings.notEmpty().apply(null);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.notNull"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }

            @Example
            @Label("should be invalid if empty")
            void shouldBeInvalidIfEmpty() {
                // when
                ValidationResult<String> result = Validators.Strings.notEmpty().apply("");

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notEmpty"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }
        }

        @Group
        @Label("notBlank")
        class StringsNotBlankTest {

            @Property
            @Label("should be valid if not blank")
            void shouldBeValidIfNotBlank(@ForAll @NotBlank String string) {
                // when
                ValidationResult<String> result = Validators.Strings.notBlank().apply(string);

                // then
                assertThat(result.toValuesEither()).containsRightSame(string);
            }

            @Example
            @Label("should be invalid if null")
            void shouldBeInvalidIfNull() {
                // when
                ValidationResult<String> result = Validators.Strings.notBlank().apply(null);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.notNull"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }

            @Property
            @Label("should be invalid if blank")
            void shouldBeInvalidIfBlank(@ForAll @CharRange(to = ' ') String string) {
                // when
                ValidationResult<String> result = Validators.Strings.notBlank().apply(string);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }
        }

        @Group
        @Label("pattern(java.lang.String)")
        class StringsPatternStringTest {
            @Property
            @Label("should be valid if matches pattern")
            void shouldBeValidIfMatchesPattern(@ForAll int i) {
                // given
                String string = Integer.toString(i);

                // when
                ValidationResult<MatchResult> result = Validators.Strings.pattern("-?[0-9]+").apply(string);

                // then
                assertThat(result.toValuesEither()).hasRightValueSatisfying(matchResult -> {
                    assertThat(matchResult.start()).isEqualTo(0);
                    assertThat(matchResult.end()).isEqualTo(string.length());
                    assertThat(matchResult.groupCount()).isEqualTo(0);
                });
            }

            @Example
            @Label("should be invalid if null")
            void shouldBeInvalidIfNull() {
                // when
                ValidationResult<MatchResult> result = Validators.Strings.pattern("-?[0-9]+").apply(null);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.notNull"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }

            @Property
            @Label("should be invalid if not matches pattern")
            void shouldBeInvalidIfNotMatchesPattern(@ForAll @AlphaChars String string) {
                // when
                ValidationResult<MatchResult> result = Validators.Strings.pattern("-?[0-9]+").apply(string);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.pattern"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }
        }

        @Group
        @Label("pattern(java.util.regex.Pattern)")
        class StringsPatternPatternTest {
            private final Pattern pattern = Pattern.compile("-?[0-9]+");

            @Property
            @Label("should be valid if matches pattern")
            void shouldBeValidIfMatchesPattern(@ForAll int i) {
                // given
                String string = Integer.toString(i);

                // when
                ValidationResult<MatchResult> result = Validators.Strings.pattern(pattern).apply(string);

                // then
                assertThat(result.toValuesEither()).hasRightValueSatisfying(matchResult -> {
                    assertThat(matchResult.start()).isEqualTo(0);
                    assertThat(matchResult.end()).isEqualTo(string.length());
                    assertThat(matchResult.groupCount()).isEqualTo(0);
                });
            }

            @Example
            @Label("should be invalid if null")
            void shouldBeInvalidIfNull() {
                // when
                ValidationResult<MatchResult> result = Validators.Strings.pattern(pattern).apply(null);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.notNull"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }

            @Property
            @Label("should be invalid if not matches pattern")
            void shouldBeInvalidIfNotMatchesPattern(@ForAll @AlphaChars String string) {
                // when
                ValidationResult<MatchResult> result = Validators.Strings.pattern(pattern).apply(string);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.pattern"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }
        }
    }

    @Group
    @Label("Integers")
    class IntegersTest {

        @Group
        @Label("min")
        class IntegersMinTest {

            @Property
            @Label("should be valid if equal or greater than minimum")
            void shouldBeValidIfEqualOrGreaterThanMinimum(@ForAll int i,
                                                          @ForAll int minimum) {
                // given
                that(i >= minimum);

                // when
                ValidationResult<Integer> result = Validators.Integers.min(minimum).apply(i);

                // then
                assertThat(result.toValuesEither()).containsOnRight(i);
            }

            @Property
            @Label("should be invalid if null")
            void shouldBeInvalidIfNull(@ForAll int minimum) {
                // when
                ValidationResult<Integer> result = Validators.Integers.min(minimum).apply(null);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.notNull"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }

            @Property
            @Label("should be invalid if less than minimum")
            void shouldBeInvalidIfLessThanMinimum(@ForAll int i,
                                                  @ForAll int minimum) {
                // given
                that(i < minimum);

                // when
                ValidationResult<Integer> result = Validators.Integers.min(minimum).apply(i);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Integers.min"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }
        }

        @Group
        @Label("max")
        class IntegersMaxTest {

            @Property
            @Label("should be valid if equal or less than maximum")
            void shouldBeValidIfEqualOrLessThanMaximum(@ForAll int i,
                                                       @ForAll int maximum) {
                // given
                that(i <= maximum);

                // when
                ValidationResult<Integer> result = Validators.Integers.max(maximum).apply(i);

                // then
                assertThat(result.toValuesEither()).containsOnRight(i);
            }

            @Property
            @Label("should be invalid if null")
            void shouldBeInvalidIfNull(@ForAll int maximum) {
                // when
                ValidationResult<Integer> result = Validators.Integers.max(maximum).apply(null);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.notNull"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }

            @Property
            @Label("should be invalid if greater than maximum")
            void shouldBeInvalidIfGreaterThanMaximum(@ForAll int i,
                                                     @ForAll int maximum) {
                // given
                that(i > maximum);

                // when
                ValidationResult<Integer> result = Validators.Integers.max(maximum).apply(i);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Integers.max"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }
        }
    }

    @Group
    @Label("Uuids")
    class UuidsTest {

        @Group
        @Label("uuidFromString")
        class UuidsUuidFromStringTest {

            @Property
            @Label("should be valid")
            void shouldBeValidIf(@ForAll long mostSigBits,
                                 @ForAll long leastSigBits) {
                // given
                UUID uuid = new UUID(mostSigBits, leastSigBits);
                String uuidString = uuid.toString();

                // when
                ValidationResult<UUID> result = Validators.Uuids.uuidFromString().apply(uuidString);

                // then
                assertThat(result.toValuesEither()).containsOnRight(uuid);
            }

            @Example
            @Label("should be invalid if null")
            void shouldBeInvalidIfNull() {
                // when
                ValidationResult<UUID> result = Validators.Uuids.uuidFromString().apply(null);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.notNull"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }

            @Property
            @Label("should be invalid")
            void shouldBeInvalid(@ForAll String string) {
                // given
                that(Try.of(() -> UUID.fromString(string)).isFailure());

                // when
                ValidationResult<UUID> result = Validators.Uuids.uuidFromString().apply(string);

                // then
                assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
                    assertThat(violations).singleElement().satisfies(violation -> {
                        assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Uuids.uuidFromString"));
                        assertThat(violation.path()).isEmpty();
                    });
                });
            }
        }
    }

    private static <A> Iterable<A> emptyIterable() {
        return () -> new Iterator<>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public A next() {
                throw new NoSuchElementException();
            }
        };
    }

    private static <A> Validator<A, A> alwaysValidValidator() {
        return ValidationResult::valid;
    }

    private static <A> Validator<A, A> alwaysInvalidValidator() {
        return __ -> invalid(violation(key("fn4j.validation.ValidatorsTest.alwaysInvalidValidator")));
    }
}