package fn4j.net.mediatype;

import io.vavr.control.Either;
import net.jqwik.api.*;

import java.util.Locale;

import static fn4j.net.mediatype.BaseType.baseType;
import static fn4j.net.mediatype.BaseType.baseTypeOrThrow;
import static net.jqwik.api.Arbitraries.chars;
import static net.jqwik.api.Arbitraries.strings;
import static net.jqwik.api.Combinators.combine;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

@Label("BaseType")
class BaseTypeTest {

    @Group
    @Label("safe")
    class Safe {

        @Property
        @Label("should be lower case")
        void shouldBeLowerCase(@ForAll("valid values maybe with upper case") String value) {
            // when
            Either<RuntimeException, BaseType> result = baseType(value);

            // then
            assertThat(result).hasRightValueSatisfying(baseType -> {
                assertThat(baseType.value()).isEqualTo(value.toLowerCase(Locale.ROOT));
            });
        }

        @Example
        @Label("should be all")
        void shouldBeAll() {
            // when
            Either<RuntimeException, BaseType> result = baseType("*");

            // then
            assertThat(result).hasRightValueSatisfying(baseType -> {
                assertThat(baseType.value()).isEqualTo("*");
            });
        }

        @Example
        @Label("should fail if null")
        void shouldFailIfNull() {
            // when
            Either<RuntimeException, BaseType> result = baseType(null);

            // then
            assertThat(result).hasLeftValueSatisfying(violation -> {
                assertThat(violation).isExactlyInstanceOf(IllegalArgumentException.class)
                                     .hasMessage("value is null");
            });
        }

        @Example
        @Label("should fail if empty")
        void shouldFailIfEmpty() {
            // when
            Either<RuntimeException, BaseType> result = baseType("");

            // then
            assertThat(result).hasLeftValueSatisfying(violation -> {
                assertThat(violation).isExactlyInstanceOf(IllegalArgumentException.class)
                                     .hasMessage("value is empty");
            });
        }

        @Property
        @Label("should fail on invalid characters")
        void shouldFailOnInvalidCharacter(@ForAll("invalid but otherwise conform values") String value) {
            // when
            Either<RuntimeException, BaseType> result = baseType(value);

            // then
            assertThat(result).hasLeftValueSatisfying(violation -> {
                assertThat(violation).isExactlyInstanceOf(IllegalArgumentException.class)
                                     .hasMessage("value contains invalid characters");
            });
        }

        @Property
        @Label("should be equal")
        void shouldBeEqual(@ForAll("valid values maybe with upper case") String value) {
            // given
            var baseType1 = baseType(value).get();
            var baseType2 = baseType(value).get();

            // when
            var result = baseType1.equals(baseType2);

            // then
            assertThat(result).isTrue();
        }

        @Property
        @Label("should be equal if case differs")
        void shouldBeEqualIfCaseDiffers(@ForAll("valid values with upper case") String value) {
            // given
            var baseType1 = baseType(value).get();
            var baseType2 = baseType(value.toLowerCase(Locale.ROOT)).get();

            // when
            var result = baseType1.equals(baseType2);

            // then
            assertThat(result).isTrue();
        }

        @Property
        @Label("should not be equal")
        void shouldNotBeEqual(@ForAll("valid values maybe with upper case") String value1,
                              @ForAll("valid values maybe with upper case") String value2) {
            // given
            Assume.that(!value1.equalsIgnoreCase(value2));
            var baseType1 = baseType(value1).get();
            var baseType2 = baseType(value2).get();

            // when
            var result = baseType1.equals(baseType2);

            // then
            assertThat(result).isFalse();
        }

        @Property
        @Label("should have hash code")
        void shouldHaveHashCode(@ForAll("valid values maybe with upper case") String value) {
            // given
            var baseType = baseType(value).get();

            // when
            var result = baseType.hashCode();

            // then
            assertThat(result).isEqualTo(value.toLowerCase(Locale.ROOT).hashCode());
        }

        @Property
        @Label("should have debug string")
        void shouldHaveDebugString(@ForAll("valid values maybe with upper case") String value) {
            // given
            var baseType = baseType(value).get();

            // when
            var result = baseType.toString();

            // then
            assertThat(result).isEqualTo("BaseType[value=%s]".formatted(value.toLowerCase(Locale.ROOT)));
        }
    }

    @Group
    @Label("unsafe")
    class Unsafe {

        @Property
        @Label("should be lower case")
        void shouldBeLowerCase(@ForAll("valid values maybe with upper case") String value) {
            // when
            BaseType result = baseTypeOrThrow(value);

            // then
            assertThat(result.value()).isEqualTo(value.toLowerCase(Locale.ROOT));
        }

        @Example
        @Label("should be all")
        void shouldBeAll() {
            // when
            BaseType result = baseTypeOrThrow("*");

            // then
            assertThat(result.value()).isEqualTo("*");
        }

        @Example
        @Label("should fail if null")
        void shouldFailIfNull() {
            // when
            Throwable result = catchThrowable(() -> baseTypeOrThrow(null));

            // then
            assertThat(result).isExactlyInstanceOf(IllegalArgumentException.class)
                              .hasMessage("value is null");
        }

        @Example
        @Label("should fail if empty")
        void shouldFailIfEmpty() {
            // when
            Throwable result = catchThrowable(() -> baseTypeOrThrow(""));

            // then
            assertThat(result).isExactlyInstanceOf(IllegalArgumentException.class)
                              .hasMessage("value is empty");
        }

        @Property
        @Label("should fail on invalid characters")
        void shouldFailOnInvalidCharacter(@ForAll("invalid but otherwise conform values") String value) {
            // when
            Throwable result = catchThrowable(() -> baseTypeOrThrow(value));

            // then
            assertThat(result).isExactlyInstanceOf(IllegalArgumentException.class)
                              .hasMessage("value contains invalid characters");
        }

        @Property
        @Label("should be equal")
        void shouldBeEqual(@ForAll("valid values maybe with upper case") String value) {
            // given
            var baseType1 = baseTypeOrThrow(value);
            var baseType2 = baseTypeOrThrow(value);

            // when
            var result = baseType1.equals(baseType2);

            // then
            assertThat(result).isTrue();
        }

        @Property
        @Label("should be equal if case differs")
        void shouldBeEqualIfCaseDiffers(@ForAll("valid values with upper case") String value) {
            // given
            var baseType1 = baseTypeOrThrow(value);
            var baseType2 = baseTypeOrThrow(value.toLowerCase(Locale.ROOT));

            // when
            var result = baseType1.equals(baseType2);

            // then
            assertThat(result).isTrue();
        }

        @Property
        @Label("should not be equal")
        void shouldNotBeEqual(@ForAll("valid values maybe with upper case") String value1,
                              @ForAll("valid values maybe with upper case") String value2) {
            // given
            Assume.that(!value1.equalsIgnoreCase(value2));
            var baseType1 = baseTypeOrThrow(value1);
            var baseType2 = baseTypeOrThrow(value2);

            // when
            var result = baseType1.equals(baseType2);

            // then
            assertThat(result).isFalse();
        }

        @Property
        @Label("should have hash code")
        void shouldHaveHashCode(@ForAll("valid values maybe with upper case") String value) {
            // given
            var baseType = baseTypeOrThrow(value);

            // when
            var result = baseType.hashCode();

            // then
            assertThat(result).isEqualTo(value.toLowerCase(Locale.ROOT).hashCode());
        }

        @Property
        @Label("should have debug string")
        void shouldHaveDebugString(@ForAll("valid values maybe with upper case") String value) {
            // given
            var baseType = baseTypeOrThrow(value);

            // when
            var result = baseType.toString();

            // then
            assertThat(result).isEqualTo("BaseType[value=%s]".formatted(value.toLowerCase(Locale.ROOT)));
        }
    }

    @Group
    @Label("safe and unsafe")
    class SafeAndUnsafe {

        @Property
        @Label("should be equal")
        void shouldBeEqual(@ForAll("valid values maybe with upper case") String value) {
            // when
            var safe = baseType(value).get();
            var unsafe = baseTypeOrThrow(value);

            // then
            assertThat(safe).isEqualTo(unsafe);
        }
    }

    @Provide("valid values")
    Arbitrary<String> validValues() {
        return combine(chars().range('a', 'z').range('0', '9'),
                       strings().withCharRange('a', 'z').withCharRange('0', '9').withChars("!#$&-^_").ofMaxLength(126))
                .as((firstCharacter, remainingCharacters) -> firstCharacter + remainingCharacters);
    }

    @Provide("valid values maybe with upper case")
    Arbitrary<String> validValuesMaybeWithUpperCase() {
        return combine(chars().range('a', 'z').range('0', '9').range('A', 'Z'),
                       strings().withCharRange('a', 'z').withCharRange('0', '9').withChars("!#$&-^_").withCharRange('A', 'Z').ofMaxLength(126))
                .as((firstCharacter, remainingCharacters) -> firstCharacter + remainingCharacters);
    }

    @Provide("valid values with upper case")
    Arbitrary<String> validValuesWithUpperCase() {
        return combine(chars().range('a', 'z').range('0', '9').range('A', 'Z'),
                       strings().withCharRange('a', 'z').withCharRange('0', '9').withChars("!#$&-^_").withCharRange('A', 'Z').ofMaxLength(126))
                .as((firstCharacter, remainingCharacters) -> firstCharacter + remainingCharacters)
                .filter(BaseTypeTest::containsUpperCase);
    }

    @Provide("invalid but otherwise conform values")
    Arbitrary<String> invalidButOtherwiseConformValues() {
        return strings().ofMinLength(1).map(value -> value.toLowerCase(Locale.ROOT));
    }

    private static boolean containsUpperCase(String string) {
        for (char c : string.toCharArray())
            if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(c) >= 0)
                return true;

        return false;
    }
}