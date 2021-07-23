package io.vavr.collection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

class SetsAddOrUpdateTest {
    @Test
    void shouldAddElementIfNotFoundByPredicate() {
        // given
        LinkedHashSet<String> set = LinkedHashSet.of("a");

        // when
        LinkedHashSet<String> result = Sets.addOrUpdate(set,
                                                        "b"::equals,
                                                        b -> fail("b must not be found"),
                                                        () -> "b");

        // then
        assertThat(result).containsExactly("a", "b");
    }

    @Test
    void shouldConsumeElementIfFoundByPredicate() {
        // given
        LinkedHashSet<Mutable> set = LinkedHashSet.of(new Mutable("a"));

        // when
        LinkedHashSet<Mutable> result = Sets.addOrUpdate(set,
                                                         mutable -> mutable.value.equals("a"),
                                                         mutable -> mutable.value = "b",
                                                         () -> fail("a must be found"));

        // then
        assertThat(result).singleElement()
                          .extracting(mutable -> mutable.value)
                          .isEqualTo("b");
    }

    private static final class Mutable {
        public String value;

        public Mutable(String value) {
            this.value = value;
        }
    }
}
