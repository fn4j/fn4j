package fn4j.http.routing;

import fn4j.net.uri.Path;
import fn4j.net.uri.PathSegment;
import io.vavr.Tuple;
import io.vavr.Tuple0;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.NotBlank;
import net.jqwik.api.constraints.Size;

import java.math.BigInteger;

import static fn4j.http.routing.PathPattern.*;
import static fn4j.http.routing.PathPatterns.*;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

@Label("Path pattern")
class PathPatternTest {

    @Property
    @Label("should match root")
    boolean shouldMatchRoot(@ForAll Path path) {
        // when
        Option<Tuple0> result = pathPattern().apply(path);

        // then
        return result.isDefined() == path.isEmpty();
    }

    @Example
    @Label("should match path element")
    void shouldMatchPathElement() {
        // given
        NotExtractingPathPattern pathPattern = pathPattern("element");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/element"));

        // then
        assertThat(result).isDefined();
    }

    @Example
    @Label("should not match path element if different")
    void shouldNotMatchPathElementIfDifferent() {
        // given
        NotExtractingPathPattern pathPattern = pathPattern("other");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/element"));

        // then
        assertThat(result).isEmpty();
    }

    @Example
    @Label("should not match path element if less elements")
    void shouldNotMatchPathElementIfLessElements() {
        // given
        NotExtractingPathPattern pathPattern = pathPattern("element").slash("sub");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/element"));

        // then
        assertThat(result).isEmpty();
    }

    @Example
    @Label("should not match path element if more elements")
    void shouldNotMatchPathElementIfMoreElements() {
        // given
        NotExtractingPathPattern pathPattern = pathPattern("element");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/element/sub"));

        // then
        assertThat(result).isEmpty();
    }

    @Example
    @Label("should match multiple path elements")
    void shouldMatchMultiplePathElements() {
        // given
        NotExtractingPathPattern pathPattern = pathPattern("element").slash("sub");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/element/sub"));

        // then
        assertThat(result).isDefined();
    }

    @Example
    @Label("should match many multiple path elements")
    void shouldMatchManyMultiplePathElements() {
        // given
        NotExtractingPathPattern pathPattern = pathPattern("a").slash("b").slash("c").slash("d").slash("e").slash("f");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/a/b/c/d/e/f"));

        // then
        assertThat(result).isDefined();
    }

    @Example
    @Label("should not match path element if different at start")
    void shouldNotMatchPathElementIfDifferentAtStart() {
        // given
        NotExtractingPathPattern pathPattern = pathPattern("a").slash("b").slash("c").slash("d").slash("e").slash("f");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/x/b/c/d/e/f"));

        // then
        assertThat(result).isEmpty();
    }

    @Example
    @Label("should not match path element if different in middle")
    void shouldNotMatchPathElementIfDifferentInMiddle() {
        // given
        NotExtractingPathPattern pathPattern = pathPattern("a").slash("b").slash("c").slash("d").slash("e").slash("f");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/a/b/x/d/e/f"));

        // then
        assertThat(result).isEmpty();
    }

    @Example
    @Label("should not match path element if different at end")
    void shouldNotMatchPathElementIfDifferentAtEnd() {
        // given
        NotExtractingPathPattern pathPattern = pathPattern("a").slash("b").slash("c").slash("d").slash("e").slash("f");

        // when
        Option<Tuple0> result = pathPattern.apply(new Path("/a/b/c/d/e/x"));

        // then
        assertThat(result).isEmpty();
    }

    @Property
    @Label("should match wildcard")
    void shouldMatchWildcard(@ForAll Path path) {
        // given
        PathSegmentsPattern1<Path> pathPattern = pathPattern(wildcard());

        // when
        Option<Path> result = pathPattern.apply(path);

        // then
        assertThat(result).isDefined()
                          .contains(path);
    }

    @Property
    @Label("should match remainder with wildcard")
    void shouldMatchRemainderWithWildcard(@ForAll Path path1,
                                          @ForAll Path path2) {
        // given
        PathSegmentsPattern1<Path> pathPattern = pathPattern(path1).slash(wildcard());
        Path path = path1.append(path2);

        // when
        Option<Path> result = pathPattern.apply(path);

        // then
        assertThat(result).isDefined()
                          .contains(path2);
    }

    @Property
    @Label("should match any segments")
    void shouldMatchAnySegments(@ForAll Path path) {
        // given
        PathSegmentsPattern0 pathPattern = pathPattern();
        for (int i = 0; i < path.pathSegments().length(); i++) {
            pathPattern = pathPattern.slash(anySegment());
        }

        // when
        Option<Tuple0> result = pathPattern.apply(path);

        // then
        assertThat(result).isDefined();
    }

    @Property
    @Label("should match string")
    void shouldMatchString(@ForAll PathSegment pathSegment) {
        // given
        var pathPattern = pathPattern(string());
        var path = new Path(pathSegment);

        // when
        Option<String> result = pathPattern.apply(path);

        // then
        assertThat(result).contains(pathSegment.value());
    }

    @Property
    @Label("should match string as first segment")
    void shouldMatchStringAsFirstSegment(@ForAll PathSegment pathSegment1,
                                         @ForAll PathSegment pathSegment2,
                                         @ForAll PathSegment pathSegment3) {
        // given
        var pathPattern = pathPattern(string()).slash(pathSegment2).slash(pathSegment3);
        var path = new Path(pathSegment1, pathSegment2, pathSegment3);

        // when
        Option<String> result = pathPattern.apply(path);

        // then
        assertThat(result).contains(pathSegment1.value());
    }

    @Property
    @Label("should match string in between")
    void shouldMatchStringInBetween(@ForAll PathSegment pathSegment1,
                                    @ForAll PathSegment pathSegment2,
                                    @ForAll PathSegment pathSegment3) {
        // given
        var pathPattern = pathPattern(pathSegment1).slash(string()).slash(pathSegment3);
        var path = new Path(pathSegment1, pathSegment2, pathSegment3);

        // when
        Option<String> result = pathPattern.apply(path);

        // then
        assertThat(result).contains(pathSegment2.value());
    }

    @Property
    @Label("should match string as last segment")
    void shouldMatchStringAsLastSegment(@ForAll PathSegment pathSegment1,
                                        @ForAll PathSegment pathSegment2,
                                        @ForAll PathSegment pathSegment3) {
        // given
        var pathPattern = pathPattern(pathSegment1).slash(pathSegment2).slash(string());
        var path = new Path(pathSegment1, pathSegment2, pathSegment3);

        // when
        Option<String> result = pathPattern.apply(path);

        // then
        assertThat(result).contains(pathSegment3.value());
    }

    @Property
    @Label("should match two strings")
    void shouldMatchTwoStrings(@ForAll @Size(5) Path path) {
        // given
        var pathPattern = pathPattern(path.get(0).get())
                .slash(string())
                .slash(path.get(2).get())
                .slash(string())
                .slash(path.get(4).get());

        // when
        Option<Tuple2<String, String>> result = pathPattern.apply(path);

        // then
        assertThat(result).contains(Tuple.of(path.get(1).get().value(),
                                             path.get(3).get().value()));
    }

    @Property
    @Label("should match custom types")
    void shouldMatchCustomType(@ForAll @NotBlank @AlphaChars String majorValue,
                               @ForAll BigInteger minorValue,
                               @ForAll @NotBlank @AlphaChars String actionValue) {
        // given
        var pathPattern = pathPattern("resource")
                .slash(Id.MATCH_SEGMENT)
                .slash(string());

        var path = new Path("/resource/%s/%s/%s".formatted(majorValue, minorValue, actionValue));

        // when
        Option<Tuple2<Id, String>> result = pathPattern.apply(path);

        // then
        assertThat(result).contains(Tuple.of(new Id(new Major(majorValue),
                                                    new Minor(minorValue)),
                                             actionValue));
    }

    private record Id(Major major, Minor minor) {
        public static final PathPattern<Id> MATCH_SEGMENT = pathPattern(Major.MATCH_SEGMENT)
                .slash(Minor.MATCH_SEGMENT)
                .map(idParts -> idParts.apply(Id::new));
    }

    private record Major(String value) {
        public static final PathPattern<Major> MATCH_SEGMENT = string().map(Major::new);
    }

    private record Minor(BigInteger value) {
        public static final PathPattern<Minor> MATCH_SEGMENT = bigInteger().map(Minor::new);
    }
}