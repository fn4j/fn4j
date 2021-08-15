package fn4j.net.uri;

import io.vavr.collection.Stream;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;

import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class PathTest {

    @Property
    @Label("should have encoding")
    void shouldHaveEncoding(@ForAll String value) {
        // given
        Path path = new Path(value);

        // when
        String result = path.encode();

        // then
        assertThat(result).isEqualTo(Stream.of(value.split("/"))
                                           .filter(segmentValue -> !segmentValue.isEmpty())
                                           .map(pathSegmentValue -> URLEncoder.encode(pathSegmentValue, UTF_8))
                                           .mkString("/", "/", ""));
    }
}