package fn4j.validation;

import fn4j.validation.Violation.MessageViolation;
import io.vavr.collection.Stream;
import net.jqwik.api.Example;

import static fn4j.validation.Validators.*;
import static fn4j.validation.Violation.key;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

class ExampleTest {
    @Example
    void should() {
        assertThat(Strings.pattern("[a-z]+").apply("ab12").toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.match"));
                assertThat(violation.path()).isEmpty();
            });
        });
        assertThat(Strings.pattern("([a-z]+)").apply("abcd").toEither()).hasRightValueSatisfying(valid -> {
            assertThat(valid.value().start()).isEqualTo(0);
            assertThat(valid.value().end()).isEqualTo(4);
            assertThat(valid.value().groupCount()).isEqualTo(1);
            assertThat(valid.value().start(0)).isEqualTo(0);
            assertThat(valid.value().end(0)).isEqualTo(4);
            assertThat(valid.value().group(0)).isEqualTo("abcd");
        });
        assertThat(Iterables.each(Strings.notBlank()).apply(Stream.of("a", "")).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
                assertThat(violation.path()).singleElement().isEqualTo("[1]");
            });
        });
        assertThat(Iterables.each(Strings.notBlank()).apply(Stream.of("", "")).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).hasSize(2).satisfies(violation -> {
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
                assertThat(violation.path()).singleElement().isEqualTo("[0]");
            }, atIndex(0)).satisfies(violation -> {
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
                assertThat(violation.path()).singleElement().isEqualTo("[1]");
            }, atIndex(1));
        });
        assertThat(Iterables.each(Strings.notBlank()).apply(Stream.of("a", "b")).toEither()).hasRightValueSatisfying(valid -> {
            assertThat(valid.value()).isEqualTo(Stream.of("a", "b"));
        });
        assertThat(Iterables.each(Strings.notBlank()).apply(Stream.empty()).toEither()).hasRightValueSatisfying(valid -> {
            assertThat(valid.value()).isEqualTo(Stream.empty());
        });
        assertThat(SumType.VALIDATOR.apply(new SumType("", 4)).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
                assertThat(violation.path()).singleElement().isEqualTo("a");
                assertThat(violation).isExactlyInstanceOf(MessageViolation.class)
                                     .asInstanceOf(type(MessageViolation.class))
                                     .satisfies(messageViolation -> {
                                         assertThat(messageViolation.message()).isEqualTo("a must not be blank");
                                     });
            });
        });
        assertThat(SumType.VALIDATOR.apply(new SumType("a", 3)).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).singleElement().satisfies(violation -> {
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Integers.greaterThanOrEqualTo"));
                assertThat(violation.path()).singleElement().isEqualTo("b");
                assertThat(violation).isExactlyInstanceOf(MessageViolation.class)
                                     .asInstanceOf(type(MessageViolation.class))
                                     .satisfies(messageViolation -> {
                                         assertThat(messageViolation.message()).isEqualTo("b must not be less than 4, but was 3");
                                     });
            });
        });
        assertThat(SumType.VALIDATOR.apply(new SumType("", 1)).toEither()).hasLeftValueSatisfying(invalid -> {
            assertThat(invalid.violations()).hasSize(2).satisfies(violation -> {
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
                assertThat(violation.path()).singleElement().isEqualTo("a");
                assertThat(violation).isExactlyInstanceOf(MessageViolation.class)
                                     .asInstanceOf(type(MessageViolation.class))
                                     .satisfies(messageViolation -> {
                                         assertThat(messageViolation.message()).isEqualTo("a must not be blank");
                                     });
            }, atIndex(0)).satisfies(violation -> {
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Integers.greaterThanOrEqualTo"));
                assertThat(violation.path()).singleElement().isEqualTo("b");
                assertThat(violation).isExactlyInstanceOf(MessageViolation.class)
                                     .asInstanceOf(type(MessageViolation.class))
                                     .satisfies(messageViolation -> {
                                         assertThat(messageViolation.message()).isEqualTo("b must not be less than 4, but was 1");
                                     });
            }, atIndex(1));
        });
        assertThat(SumType.VALIDATOR.apply(new SumType("a", 4)).toEither()).hasRightValueSatisfying(valid -> {
            assertThat(valid.value()).isEqualTo(new SumType("a", 4));
        });
    }

    static record SumType(String a,
                          int b) {
        private static final Validator<String, String> A_VALIDATOR = Strings.notBlank().withMessage(__ -> "a must not be blank");
        private static final Validator<Integer, Integer> B_VALIDATOR = Integers.min(4).withMessage(actual -> "b must not be less than 4, but was " + actual);

        static final Validator<SumType, SumType> VALIDATOR = Validator.ofAll(A_VALIDATOR.as("a", SumType::a),
                                                                             B_VALIDATOR.as("b", SumType::b));
    }
}