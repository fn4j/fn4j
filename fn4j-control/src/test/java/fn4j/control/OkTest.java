package fn4j.control;

import io.vavr.collection.Iterator;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.*;

class OkTest {
    @Test
    void shouldBeOk() {
        // given
        Result<A, B> result = Result.ok(b);

        // when
        boolean r = result.isOk();

        // then
        assertThat(r).isTrue();
    }

    @Test
    void shouldNotBeError() {
        // given
        Result<A, B> result = Result.ok(b);

        // when
        boolean r = result.isError();

        // then
        assertThat(r).isFalse();
    }

    @Test
    void shouldHaveOkValue() {
        // given
        Result<A, B> result = Result.ok(b);

        // when
        B r = result.get();

        // then
        assertThat(r).isSameAs(b);
    }

    @Test
    void shouldNotHaveError() {
        // given
        Result<A, B> result = Result.ok(b);

        // when
        NoSuchElementException r = catchThrowableOfType(result::getError, NoSuchElementException.class);

        // then
        assertThat(r).hasMessage("getError() on Ok");
    }

    @Test
    void shouldNotBeAsync() {
        // given
        Result<A, B> result = Result.ok(b);

        // when
        boolean r = result.isAsync();

        // then
        assertThat(r).isFalse();
    }

    @Test
    void shouldNotBeLazy() {
        // given
        Result<A, B> result = Result.ok(b);

        // when
        boolean r = result.isLazy();

        // then
        assertThat(r).isFalse();
    }

    @Test
    void shouldBeSingleValued() {
        // given
        Result<A, B> result = Result.ok(b);

        // when
        boolean r = result.isSingleValued();

        // then
        assertThat(r).isTrue();
    }

    @Test
    void shouldHaveStringRepresentation() {
        // given
        Result<A, B> result = Result.ok(b);

        // when
        String r = result.toString();

        // then
        assertThat(r).isEqualTo("Ok(B)");
    }

    @Test
    void shouldHaveIterator() {
        // given
        Result<A, B> result = Result.ok(b);

        // when
        Iterator<B> r = result.iterator();

        // then
        assertThat(r.toJavaList()).singleElement()
                                  .isSameAs(b);
    }

    @Test
    void shouldNotBeEmpty() {
        // given
        Result<A, B> result = Result.ok(b);

        // when
        boolean r = result.isEmpty();

        // then
        assertThat(r).isFalse();
    }

    @Test
    void shouldPeek() {
        // given
        Result<A, B> result = Result.ok(b);
        AtomicBoolean consumed = new AtomicBoolean(false);

        // when
        Result<A, B> r = result.peek((B __) -> consumed.set(true));

        // then
        assertThat(consumed).isTrue();
        assertThat(r).isSameAs(result);
    }

    @Test
    void shouldFlatMapToError() {
        // given
        Result<A, B> result = Result.ok(b);
        Result<A, C> result2 = Result.error(a);

        // when
        Result<A, C> r = result.flatMap(_b -> {
            assertThat(_b).isSameAs(b);
            return result2;
        });

        // then
        assertThat(r).isSameAs(result2);
    }

    @Test
    void shouldFlatMapToOk() {
        // given
        Result<A, B> result = Result.ok(b);
        Result<A, C> result2 = Result.ok(c);

        // when
        Result<A, C> r = result.flatMap(_b -> {
            assertThat(_b).isSameAs(b);
            return result2;
        });

        // then
        assertThat(r).isSameAs(result2);
    }

    @Test
    void shouldMap() {
        // given
        Result<A, B> result = Result.ok(b);

        // when
        Result<A, C> r = result.map(_b -> {
            assertThat(_b).isSameAs(b);
            return c;
        });

        // then
        assertThat(r.get()).isSameAs(c);
    }

    @Test
    void shouldMapErrorAsSame() {
        // given
        Result<A, B> result = Result.ok(b);

        // when
        Result<C, B> r = result.mapError(_b -> fail("mapper must not be called"));

        // then
        assertThat(r).isSameAs(result);
    }

    @Test
    void shouldBimap() {
        // given
        Result<A, B> result = Result.ok(b);

        // when
        Result<C, D> r = result.biMap(__ -> fail("errorMapper must not be called"),
                                      _b -> {
                                          assertThat(_b).isSameAs(b);
                                          return d;
                                      });

        // then
        assertThat(r.get()).isSameAs(d);
    }

    private final A a = new A();
    private final B b = new B();
    private final C c = new C();
    private final D d = new D();

    private static class A {
    }

    private static class B {
        @Override
        public String toString() {
            return "B";
        }
    }

    private static class C {
    }

    private static class D {
    }
}