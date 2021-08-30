package fn4j.validation;

import io.vavr.control.Try;
import net.jqwik.api.*;
import net.jqwik.api.constraints.CharRange;
import net.jqwik.api.constraints.NotBlank;
import net.jqwik.api.constraints.NotEmpty;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

import static fn4j.validation.Violation.key;
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
            void shouldBeInvalidIfEmpty() {
                // given
                Iterable<Object> emptyIterable = () -> new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public Object next() {
                        throw new NoSuchElementException();
                    }
                };

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

        // TODO: each
    }

    @Group
    @Label("Strings")
    class StringsTest {

        @Group
        @Label("notBlank")
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

        // TODO: pattern
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
                Assume.that(i >= minimum);

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
                Assume.that(i < minimum);

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
                Assume.that(i <= maximum);

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
                Assume.that(i > maximum);

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

            @Property
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
                Assume.that(Try.of(() -> UUID.fromString(string)).isFailure());

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
}