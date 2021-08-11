package fn4j.http.routing;

import fn4j.net.uri.Path;
import io.vavr.Tuple0;
import io.vavr.control.Option;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;

import static fn4j.http.routing.PathPattern.Root;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

@Label("Path pattern")
class PathPatternTest {

    @Property
    @Label("should match root")
    boolean shouldMatchRoot(@ForAll Path path) {
        // when
        Option<Tuple0> result = Root.apply(path);

        // then
        return result.isDefined() == path.isEmpty();
    }

    @Example
    @Label("should match path element")
    void shouldMatchPathElement() {
        // given
        PathPattern<Tuple0> pathPattern = Root.slash("element");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/element"));

        // then
        assertThat(result).isDefined();
    }

    @Example
    @Label("should not match path element if different")
    void shouldNotMatchPathElementIfDifferent() {
        // given
        PathPattern<Tuple0> pathPattern = Root.slash("other");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/element"));

        // then
        assertThat(result).isEmpty();
    }

    @Example
    @Label("should not match path element if less elements")
    void shouldNotMatchPathElementIfLessElements() {
        // given
        PathPattern<Tuple0> pathPattern = Root.slash("element").slash("sub");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/element"));

        // then
        assertThat(result).isEmpty();
    }

    @Example
    @Label("should not match path element if more elements")
    void shouldNotMatchPathElementIfMoreElements() {
        // given
        PathPattern<Tuple0> pathPattern = Root.slash("element");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/element/sub"));

        // then
        assertThat(result).isEmpty();
    }

    @Example
    @Label("should match multiple path elements")
    void shouldMatchMultiplePathElements() {
        // given
        PathPattern<Tuple0> pathPattern = Root.slash("element").slash("sub");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/element/sub"));

        // then
        assertThat(result).isDefined();
    }

    @Example
    @Label("should match many multiple path elements")
    void shouldMatchManyMultiplePathElements() {
        // given
        PathPattern<Tuple0> pathPattern = Root.slash("a").slash("b").slash("c").slash("d").slash("e").slash("f");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/a/b/c/d/e/f"));

        // then
        assertThat(result).isDefined();
    }

    @Example
    @Label("should not match path element if different at start")
    void shouldNotMatchPathElementIfDifferentAtStart() {
        // given
        PathPattern<Tuple0> pathPattern = Root.slash("a").slash("b").slash("c").slash("d").slash("e").slash("f");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/x/b/c/d/e/f"));

        // then
        assertThat(result).isEmpty();
    }

    @Example
    @Label("should not match path element if different in middle")
    void shouldNotMatchPathElementIfDifferentInMiddle() {
        // given
        PathPattern<Tuple0> pathPattern = Root.slash("a").slash("b").slash("c").slash("d").slash("e").slash("f");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/a/b/x/d/e/f"));

        // then
        assertThat(result).isEmpty();
    }

    @Example
    @Label("should not match path element if different at end")
    void shouldNotMatchPathElementIfDifferentAtEnd() {
        // given
        PathPattern<Tuple0> pathPattern = Root.slash("a").slash("b").slash("c").slash("d").slash("e").slash("f");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/a/b/c/d/e/x"));

        // then
        assertThat(result).isEmpty();
    }
}