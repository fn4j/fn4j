package fn4j.net.uri;

import io.vavr.API;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

import static fn4j.net.uri.Literal.AMPERSAND;
import static fn4j.net.uri.Literal.EQUALS;
import static io.vavr.API.Seq;
import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;

public record QueryParameters(Seq<Tuple2<QueryParameterKey, QueryParameterValue>> value) implements UriComponentParent {
    public QueryParameters(Option<Query> maybeQuery) {
        this(parse(maybeQuery));
    }

    public QueryParameters(Query query) {
        this(parse(query.value()));
    }

    public QueryParameters(String query) {
        this(parse(query));
    }

    public Query query() {
        return new Query(encode());
    }

    @Override
    public Seq<UriComponent> components() {
        return Stream.concat(value.map(queryParameter -> Stream.<UriComponent>of(queryParameter._1(), EQUALS, queryParameter._2()))
                                  .intersperse(Stream.of(AMPERSAND)));
    }

    private static Seq<Tuple2<QueryParameterKey, QueryParameterValue>> parse(Option<Query> maybeQuery) {
        return maybeQuery.fold(API::Seq, query -> parse(query.value()));
    }

    private static Seq<Tuple2<QueryParameterKey, QueryParameterValue>> parse(String query) {
        Seq<Tuple2<QueryParameterKey, QueryParameterValue>> parameters = Seq();
        var continuationIndex = 0;

        while (continuationIndex < query.length()) {
            final var keyValueDelimiterIndex = query.indexOf('=', continuationIndex);
            final var parameterDelimiterIndex = query.indexOf('&', continuationIndex);

            final int keyStartIndex;
            final int keyEndIndex;
            final int valueStartIndex;
            final int valueEndIndex;

            if (keyValueDelimiterIndex < 0) {
                if (parameterDelimiterIndex < 0) {
                    keyStartIndex = continuationIndex;
                    keyEndIndex = query.length();
                    valueStartIndex = keyEndIndex;
                    valueEndIndex = keyEndIndex;
                } else {
                    keyStartIndex = continuationIndex;
                    keyEndIndex = parameterDelimiterIndex;
                    valueStartIndex = keyEndIndex;
                    valueEndIndex = keyEndIndex;
                }
            } else {
                if (parameterDelimiterIndex < 0) {
                    keyStartIndex = continuationIndex;
                    keyEndIndex = keyValueDelimiterIndex;
                    valueStartIndex = keyValueDelimiterIndex + 1;
                    valueEndIndex = query.length();
                } else {
                    if (parameterDelimiterIndex < keyValueDelimiterIndex) {
                        keyStartIndex = continuationIndex;
                        keyEndIndex = parameterDelimiterIndex;
                        valueStartIndex = keyEndIndex;
                        valueEndIndex = keyEndIndex;
                    } else {
                        keyStartIndex = continuationIndex;
                        keyEndIndex = keyValueDelimiterIndex;
                        valueStartIndex = keyValueDelimiterIndex + 1;
                        valueEndIndex = parameterDelimiterIndex;
                    }
                }
            }

            final var key = new QueryParameterKey(decode(query.substring(keyStartIndex, keyEndIndex), UTF_8));
            final var value = new QueryParameterValue(decode(query.substring(valueStartIndex, valueEndIndex), UTF_8));
            parameters = parameters.append(Tuple.of(key, value));

            continuationIndex = valueEndIndex + 1;
        }

        return parameters.filter(parameter -> parameter._1().value().length() > 0);
    }
}