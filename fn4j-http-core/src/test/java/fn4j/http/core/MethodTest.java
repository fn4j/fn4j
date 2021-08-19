package fn4j.http.core;

import net.jqwik.api.*;

import static fn4j.http.core.Method.COMMON_METHODS;
import static net.jqwik.api.Arbitraries.*;
import static org.assertj.core.api.Assertions.assertThat;

@Label("Method")
class MethodTest {

    @Group
    @Label("Ordering")
    class Ordering {

        @Property
        @Label("should have defined ordering for common methods")
        void shouldHaveDefinedOrderingForCommonMethods(@ForAll("methods") Method method1,
                                                       @ForAll("methods") Method method2) {
            // given
            Assume.that(!method1.equals(method2));
            Assume.that(method1.isCommon());
            Assume.that(method2.isCommon());

            // when
            int result = method1.compareTo(method2);

            // then
            var method1Index = COMMON_METHODS.toStream().indexOf(method1);
            var method2Index = COMMON_METHODS.toStream().indexOf(method2);
            assertThat(result).isEqualTo(Integer.compare(method1Index, method2Index));
        }

        @Property
        @Label("should order uncommon methods by their value ignoring the case")
        void shouldOrderUncommonMethodsByTheirValueIgnoringTheCase(@ForAll("methods") Method method1,
                                                                   @ForAll("methods") Method method2) {
            // given
            Assume.that(!method1.equals(method2));
            Assume.that(!method1.isCommon());
            Assume.that(!method2.isCommon());

            // when
            int result = method1.compareTo(method2);

            // then
            assertThat(result).isEqualTo(method1.value().compareToIgnoreCase(method2.value()));
        }

        @Property
        @Label("should order common methods before uncommon ones")
        void shouldOrderCommonMethodsBeforeUncommonOnes(@ForAll("methods") Method method1,
                                                        @ForAll("methods") Method method2) {
            // given
            Assume.that(!method1.equals(method2));
            Assume.that(method1.isCommon());
            Assume.that(!method2.isCommon());

            // when
            int result = method1.compareTo(method2);

            // then
            assertThat(result).isEqualTo(-1);
        }

        @Property
        @Label("should order uncommon methods after common ones")
        void shouldOrderUncommonMethodsAfterCommonOnes(@ForAll("methods") Method method1,
                                                       @ForAll("methods") Method method2) {
            // given
            Assume.that(!method1.equals(method2));
            Assume.that(!method1.isCommon());
            Assume.that(method2.isCommon());

            // when
            int result = method1.compareTo(method2);

            // then
            assertThat(result).isEqualTo(1);
        }
    }

    @Provide
    @SuppressWarnings("unchecked")
    public static Arbitrary<Method> methods() {
        return oneOf(commonMethods(), uncommonMethods());
    }

    public static Arbitrary<Method> commonMethods() {
        return of(COMMON_METHODS.toJavaList());
    }

    public static Arbitrary<Method> uncommonMethods() {
        return strings().withCharRange('A', 'Z')
                        .ofMinLength(1)
                        .ofMaxLength(10)
                        .map(Method::new);
    }
}