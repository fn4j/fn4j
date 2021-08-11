package fn4j.net.uri;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Try;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.vavr.API.Seq;
import static java.net.URLDecoder.decode;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static net.jqwik.api.Arbitraries.integers;
import static net.jqwik.api.Arbitraries.strings;
import static net.jqwik.api.Combinators.combine;
import static net.jqwik.api.RandomDistribution.gaussian;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.assertj.vavr.api.VavrAssertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class QueryParametersTest {
    @ParameterizedTest
    @MethodSource
    void shouldHaveQueryParameters(String query,
                                   Tuple2<QueryParameterKey, QueryParameterValue>[] queryParameters) {
        // when
        QueryParameters result = new QueryParameters(query);

        // then
        assertThat(result.value()).containsExactly(queryParameters);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> shouldHaveQueryParameters() {
        return Seq(Seq("tag=networking&order=newest", "tag", "networking", "order", "newest"),
                   Seq("a=b", "a", "b"),
                   Seq("a=b&c=d", "a", "b", "c", "d"),
                   Seq("a=b&c=d&e=f", "a", "b", "c", "d", "e", "f"),
                   Seq(""),
                   Seq(" ", " ", ""),
                   Seq("a", "a", ""),
                   Seq("a ", "a ", ""),
                   Seq(" a", " a", ""),
                   Seq("a=", "a", ""),
                   Seq("a= ", "a", " "),
                   Seq("a=+", "a", " "),
                   Seq("a==", "a", "="),
                   Seq("a=/", "a", "/"),
                   Seq("a=&", "a", ""),
                   Seq("a=&&", "a", ""),
                   Seq("&"),
                   Seq("="),
                   Seq("a", "a", ""),
                   Seq("a&b", "a", "", "b", ""),
                   Seq("a=b&c", "a", "b", "c", ""),
                   Seq("a&b=c", "a", "", "b", "c"),
                   Seq("a&b&c", "a", "", "b", "", "c", ""),
                   Seq("a&b=c&d=e", "a", "", "b", "c", "d", "e"),
                   Seq("a=b&c&d=e", "a", "b", "c", "", "d", "e"),
                   Seq("a=b&c=d&e", "a", "b", "c", "d", "e", ""),
                   Seq("a&b&c=d", "a", "", "b", "", "c", "d"),
                   Seq("a&b=c&d", "a", "", "b", "c", "d", ""),
                   Seq("a=b&c&d", "a", "b", "c", "", "d", ""),
                   Seq("a=%3B%2C%2F%3F%3A%40%26%3D%2B%24", "a", ";,/?:@&=+$"),
                   Seq("a=-_.!~*'()", "a", "-_.!~*'()"),
                   Seq("a=%23", "a", "#"),
                   Seq("a=", "a", ""),
                   Seq("a=ABC%20abc%20123", "a", "ABC abc 123"))
                .map(testParameters -> arguments(testParameters.head(),
                                                 testParameters.tail()
                                                               .grouped(2)
                                                               .map(queryParameters -> Tuple.of(new QueryParameterKey(queryParameters.get(0)),
                                                                                                new QueryParameterValue(queryParameters.get(1))))
                                                               .toJavaArray(Tuple2[]::new)))
                .toJavaStream();
    }

    @Property
    void shouldHaveEncodedQuery(@ForAll("queryParameters") QueryParameters queryParameters) {
        // when
        var result = queryParameters.query();

        // then
        assertThat(result.value()).isEqualTo(queryParameters.value().map(queryParameter -> {
            var key = encode(queryParameter._1().value(), UTF_8);
            var value = encode(queryParameter._2().value(), UTF_8);
            return key + "=" + value;
        }).mkString("&"));
    }

    @Property
    void shouldBeDecodedFromQuery(@ForAll("queryParameters") QueryParameters queryParameters) {
        // when
        QueryParameters result = new QueryParameters(queryParameters.query());

        // then
        assertThat(result).isEqualTo(queryParameters);
    }

    @Property
    void shouldNotThrow(@ForAll("urlDecodeSafeStrings") String urlDecodeSafeString) {
        assertThat(catchThrowable(() -> new QueryParameters(urlDecodeSafeString))).isNull();
    }

    @Provide
    Arbitrary<QueryParameters> queryParameters() {
        return integers().between(0, 10)
                         .withDistribution(gaussian())
                         .flatMap(size -> combine(urlDecodeSafeStrings(false).list()
                                                                             .ofSize(size)
                                                                             .map(List::ofAll)
                                                                             .map(keys -> keys.map(QueryParameterKey::new)),
                                                  urlDecodeSafeStrings().list()
                                                                        .ofSize(size)
                                                                        .map(List::ofAll)
                                                                        .map(values -> values.map(QueryParameterValue::new))).as(List::zip))
                         .map(QueryParameters::new);

    }

    @Provide
    Arbitrary<String> urlDecodeSafeStrings() {
        return urlDecodeSafeStrings(true);
    }

    Arbitrary<String> urlDecodeSafeStrings(boolean includeEmpty) {
        return (
                includeEmpty ?
                        strings() :
                        strings().ofMinLength(1)
        )
                .withChars(";,/?:@&=+$-_.!~*'()# ")
                .filter(string -> Try.of(() -> decode(string, UTF_8)).isSuccess());
    }
}