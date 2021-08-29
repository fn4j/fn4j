package fn4j.validation;

import net.jqwik.api.*;
import net.jqwik.api.constraints.NotBlank;
import net.jqwik.api.constraints.NotEmpty;
import net.jqwik.api.constraints.Whitespace;

import java.util.Iterator;
import java.util.NoSuchElementException;

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

            @Example
            @Label("should be invalid if blank")
            void shouldBeInvalidIfBlank(@ForAll @Whitespace String string) {
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

        // TODO: notEmpty
        // TODO: pattern
    }

    // TODO: integers
    // TODO: uuids
}