package fn4j.net.uri;

import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

import java.net.URI;

import static fn4j.net.uri.Literal.*;

public record Uri(Option<Scheme> maybeScheme,
                  Option<Authority> maybeAuthority,
                  Path path,
                  Option<Query> maybeQuery,
                  Option<Fragment> maybeFragment) implements UriComponentParent {
    public Uri(String maybeValue) {
        this(URI.create(Option.of(maybeValue)
                              .map(Uri::injectSlashAfterFirstColon)
                              .getOrElse("")));
    }

    public Uri(URI uri) {
        this(Option.of(uri.getScheme()).map(Scheme::new),
             Option.of(uri.getHost())
                   .map(host -> new Authority(Option.of(uri.getUserInfo()).map(UserInfo::new),
                                              new Host(host),
                                              Option.when(uri.getPort() != -1, uri.getPort()).map(Port::new))),
             new Path(uri.getPath()),
             Option.of(uri.getQuery()).map(Query::new),
             Option.of(uri.getFragment()).map(Fragment::new));
    }

    public QueryParameters queryParameters() {
        return new QueryParameters(maybeQuery);
    }

    @Override
    public Seq<UriComponent> components() {
        return Stream.<UriComponent>ofAll(maybeScheme).append(COLON)
                     .appendAll(maybeAuthority.toStream().flatMap(authority -> Stream.<UriComponent>of(DOUBLE_SLASH).appendAll(authority.components())))
                     .appendAll(path.components())
                     .appendAll(maybeQuery.toStream().flatMap(query -> Stream.<UriComponent>of(QUESTION_MARK, query)))
                     .appendAll(maybeFragment.toStream().flatMap(fragment -> Stream.<UriComponent>of(HASH, fragment)));
    }

    public URI asJavaURI() {
        return URI.create(encode());
    }

    /**
     * Without injecting a slash after the first colon in URIs that have no
     * slash, {@link URI#create(String)} drops the path for URIs like
     * {@code scheme:path}.
     */
    private static String injectSlashAfterFirstColon(String value) {
        if (value.contains(":") && !value.contains("/")) {
            var indexOfFirstColon = value.indexOf(':');
            return value.substring(0, indexOfFirstColon) + ":/" + value.substring(indexOfFirstColon + 1);
        }

        return value;
    }
}