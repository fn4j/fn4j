package fn4j.validation;

import fn4j.validation.Violation.MessageViolation;
import net.jqwik.api.Example;
import net.jqwik.api.Label;

import static fn4j.validation.Validators.Integers;
import static fn4j.validation.Validators.Strings;
import static fn4j.validation.Violation.key;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

@Label("Product type validator")
class ProductTypeValidatorTest {

    @Label("should be invalid if one term is invalid")
    @Example
    void shouldBeInvalidIfOneTypeIsInvalid() {
        // given
        var productType = new ProductType("", 4);

        // when
        var result = ProductType.VALIDATOR.apply(productType);

        // then
        assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
            assertThat(violations).singleElement().satisfies(violation -> {
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
                assertThat(violation.path()).singleElement().isEqualTo("a");
                assertThat(violation).isExactlyInstanceOf(MessageViolation.class)
                                     .asInstanceOf(type(MessageViolation.class))
                                     .satisfies(messageViolation -> {
                                         assertThat(messageViolation.message()).isEqualTo("a must not be blank");
                                     });
            });
        });
    }

    @Label("should be invalid if another term is invalid")
    @Example
    void shouldBeInvalidIfAnotherTypeIsInvalid() {
        // given
        var productType = new ProductType("a", 3);

        // when
        var result = ProductType.VALIDATOR.apply(productType);

        // then
        assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
            assertThat(violations).singleElement().satisfies(violation -> {
                assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Integers.min"));
                assertThat(violation.path()).singleElement().isEqualTo("b");
                assertThat(violation).isExactlyInstanceOf(MessageViolation.class)
                                     .asInstanceOf(type(MessageViolation.class))
                                     .satisfies(messageViolation -> {
                                         assertThat(messageViolation.message()).isEqualTo("b must not be less than 4, but was 3");
                                     });
            });
        });
    }

    @Label("should be invalid if all terms are invalid")
    @Example
    void shouldBeInvalidIfAllTermsAreInvalid() {
        // given
        var productType = new ProductType("", 1);

        // when
        var result = ProductType.VALIDATOR.apply(productType);

        // then
        assertThat(result.toValuesEither()).hasLeftValueSatisfying(violations -> {
            assertThat(violations).hasSize(2)
                                  .satisfies(violation -> {
                                      assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Strings.notBlank"));
                                      assertThat(violation.path()).singleElement().isEqualTo("a");
                                      assertThat(violation).isExactlyInstanceOf(MessageViolation.class)
                                                           .asInstanceOf(type(MessageViolation.class))
                                                           .satisfies(messageViolation -> {
                                                               assertThat(messageViolation.message()).isEqualTo("a must not be blank");
                                                           });
                                  }, atIndex(0))
                                  .satisfies(violation -> {
                                      assertThat(violation.key()).isEqualTo(key("fn4j.validation.Validators.Integers.min"));
                                      assertThat(violation.path()).singleElement().isEqualTo("b");
                                      assertThat(violation).isExactlyInstanceOf(MessageViolation.class)
                                                           .asInstanceOf(type(MessageViolation.class))
                                                           .satisfies(messageViolation -> {
                                                               assertThat(messageViolation.message()).isEqualTo("b must not be less than 4, but was 1");
                                                           });
                                  }, atIndex(1));
        });
    }

    @Label("should be valid if all terms are valid")
    @Example
    void shouldBeValidIfAllTermsAreValid() {
        // given
        var productType = new ProductType("a", 4);

        // when
        var result = ProductType.VALIDATOR.apply(productType);

        // then
        assertThat(result.toValuesEither()).containsRightSame(productType);
    }

    record ProductType(String a,
                       int b) {
        private static final Validator<String, String> A_VALIDATOR = Strings.notBlank().withMessage(__ -> "a must not be blank");
        private static final Validator<Integer, Integer> B_VALIDATOR = Integers.min(4).withMessage(actual -> "b must not be less than 4, but was " + actual);

        static final Validator<ProductType, ProductType> VALIDATOR = Validator.ofAll(A_VALIDATOR.as("a", ProductType::a),
                                                                                     B_VALIDATOR.as("b", ProductType::b));
    }
}