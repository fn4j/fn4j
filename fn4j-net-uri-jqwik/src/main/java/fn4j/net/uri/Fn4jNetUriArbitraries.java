package fn4j.net.uri;

import io.vavr.collection.Stream;
import io.vavr.control.Option;
import io.vavr.control.Try;
import net.jqwik.api.Arbitrary;

import static java.net.URLDecoder.decode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static net.jqwik.api.Arbitraries.*;
import static net.jqwik.api.Combinators.combine;
import static net.jqwik.api.RandomDistribution.gaussian;

public final class Fn4jNetUriArbitraries {
    private Fn4jNetUriArbitraries() {
    }

    public static Arbitrary<Authority> authorities() {
        return combine(userInfo().optional().map(Option::ofOptional),
                       hosts(),
                       ports().optional().map(Option::ofOptional)).as(Authority::new);
    }

    public static Arbitrary<Fragment> fragments() {
        return strings().map(Fragment::new);
    }

    public static Arbitrary<Host> hosts() {
        return strings().alpha().map(Host::new);
    }

    public static PathArbitrary paths() {
        return new PathArbitrary();
    }

    public static Arbitrary<PathSegment> pathSegments() {
        return urlDecodeSafeStrings(false, '/').map(PathSegment::new);
    }

    public static Arbitrary<Port> ports() {
        return integers().between(0, 65535).map(Port::new);
    }

    @SuppressWarnings("unchecked")
    public static Arbitrary<Query> queries() {
        return oneOf(urlDecodeSafeStrings(true).map(Query::new),
                     queryParameters().map(QueryParameters::query));
    }

    public static Arbitrary<QueryParameterKey> queryParameterKeys() {
        return urlDecodeSafeStrings(false).map(QueryParameterKey::new);
    }

    public static Arbitrary<QueryParameters> queryParameters() {
        return integers().between(0, 10)
                         .withDistribution(gaussian())
                         .flatMap(size -> combine(queryParameterKeys().list()
                                                                      .ofSize(size)
                                                                      .map(Stream::ofAll),
                                                  queryParameterValues().list()
                                                                        .ofSize(size)
                                                                        .map(Stream::ofAll)).as(Stream::zip))
                         .map(QueryParameters::new);
    }

    public static Arbitrary<QueryParameterValue> queryParameterValues() {
        return urlDecodeSafeStrings(true).map(QueryParameterValue::new);
    }

    public static Arbitrary<Scheme> schemes() {
        return oneOf(of("http", "https", "ftp"),
                     strings().alpha()).map(Scheme::new);
    }

    public static Arbitrary<Uri> uris() {
        return combine(schemes().optional().map(Option::ofOptional),
                       authorities().optional().map(Option::ofOptional),
                       paths(),
                       queries().optional().map(Option::ofOptional),
                       fragments().optional().map(Option::ofOptional)).as(Uri::new);
    }

    @SuppressWarnings("unchecked")
    public static Arbitrary<UriComponent> uriComponents() {
        return oneOf(authorities(),
                     fragments(),
                     hosts(),
                     paths(),
                     pathSegments(),
                     ports(),
                     queries(),
                     queryParameterKeys(),
                     queryParameters(),
                     queryParameterValues(),
                     schemes(),
                     uris(),
                     userInfo());
    }

    @SuppressWarnings("unchecked")
    public static Arbitrary<UriComponentParent> uriComponentParents() {
        return oneOf(authorities(),
                     paths(),
                     queryParameters(),
                     uris());
    }

    public static Arbitrary<UserInfo> userInfo() {
        return strings().alpha().numeric().withChars(':').map(UserInfo::new);
    }

    public static Arbitrary<String> urlDecodeSafeStrings(boolean includeEmpty,
                                                         char... charsToExclude) {
        return strings().ofMinLength(includeEmpty ? 0 : 1)
                        .excludeChars(charsToExclude)
                        .filter(string -> Try.of(() -> decode(string, UTF_8)).isSuccess());
    }
}