package fn4j.net.uri;

import io.vavr.control.Option;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static io.vavr.API.Seq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class UriTest {
    @ParameterizedTest
    @MethodSource
    void shouldParse(String value,
                     Uri uri) {
        assertThat(new Uri(value)).isEqualTo(uri);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> shouldParse() {
        return Stream.of(arguments(null,
                                   new Uri(Option.none(),
                                           Option.none(),
                                           new Path(Seq()),
                                           Option.none(),
                                           Option.none())),
                         arguments("",
                                   new Uri(Option.none(),
                                           Option.none(),
                                           new Path(Seq()),
                                           Option.none(),
                                           Option.none())),
                         arguments("/",
                                   new Uri(Option.none(),
                                           Option.none(),
                                           new Path(Seq()),
                                           Option.none(),
                                           Option.none())),
                         arguments("a",
                                   new Uri(Option.none(),
                                           Option.none(),
                                           new Path(Seq(new PathSegment("a"))),
                                           Option.none(),
                                           Option.none())),
                         arguments("https://john.doe@www.example.com:123/forum/questions/?tag=networking&order=newest#top",
                                   new Uri(Option.of(new Scheme("https")),
                                           Option.of(new Authority(Option.of(new UserInfo("john.doe")),
                                                                   new Host("www.example.com"),
                                                                   Option.of(new Port(123)))),
                                           new Path(Seq(new PathSegment("forum"),
                                                        new PathSegment("questions"))),
                                           Option.of(new Query("tag=networking&order=newest")),
                                           Option.of(new Fragment("top")))),
                         arguments("ldap://[2001:db8::7]/c=GB?objectClass?one",
                                   new Uri(Option.of(new Scheme("ldap")),
                                           Option.of(new Authority(Option.none(),
                                                                   new Host("[2001:db8::7]"),
                                                                   Option.none())),
                                           new Path(Seq(new PathSegment("c=GB"))),
                                           Option.of(new Query("objectClass?one")),
                                           Option.none())),
                         arguments("mailto:John.Doe@example.com",
                                   new Uri(Option.of(new Scheme("mailto")),
                                           Option.none(),
                                           new Path(Seq(new PathSegment("John.Doe@example.com"))),
                                           Option.none(),
                                           Option.none())),
                         arguments("news:comp.infosystems.www.servers.unix",
                                   new Uri(Option.of(new Scheme("news")),
                                           Option.none(),
                                           new Path(Seq(new PathSegment("comp.infosystems.www.servers.unix"))),
                                           Option.none(),
                                           Option.none())),
                         arguments("tel:+1-816-555-1212",
                                   new Uri(Option.of(new Scheme("tel")),
                                           Option.none(),
                                           new Path(Seq(new PathSegment("+1-816-555-1212"))),
                                           Option.none(),
                                           Option.none())),
                         arguments("telnet://192.0.2.16:80/",
                                   new Uri(Option.of(new Scheme("telnet")),
                                           Option.of(new Authority(Option.none(),
                                                                   new Host("192.0.2.16"),
                                                                   Option.of(new Port(80)))),
                                           new Path(Seq()),
                                           Option.none(),
                                           Option.none())),
                         arguments("urn:oasis:names:specification:docbook:dtd:xml:4.1.2",
                                   new Uri(Option.of(new Scheme("urn")),
                                           Option.none(),
                                           new Path(Seq(new PathSegment("oasis:names:specification:docbook:dtd:xml:4.1.2"))),
                                           Option.none(),
                                           Option.none())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"//"})
    void shouldNotParse(String value) {
        assertThat(catchThrowable(() -> new Uri(value))).isNotNull();
    }
}