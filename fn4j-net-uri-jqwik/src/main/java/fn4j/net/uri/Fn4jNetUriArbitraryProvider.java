package fn4j.net.uri;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import static fn4j.net.uri.Fn4jNetUriArbitraries.*;

public class Fn4jNetUriArbitraryProvider implements ArbitraryProvider {
    private static final Map<Class<?>, Arbitrary<?>> MAPPINGS =
            Map.ofEntries(entry(Authority.class, authorities()),
                          entry(Fragment.class, fragments()),
                          entry(Host.class, hosts()),
                          entry(Path.class, paths()),
                          entry(PathSegment.class, pathSegments()),
                          entry(Port.class, ports()),
                          entry(Query.class, queries()),
                          entry(QueryParameterKey.class, queryParameterKeys()),
                          entry(QueryParameters.class, queryParameters()),
                          entry(QueryParameterValue.class, queryParameterValues()),
                          entry(Scheme.class, schemes()),
                          entry(Uri.class, uris()),
                          entry(UriComponent.class, uriComponents()),
                          entry(UriComponentParent.class, uriComponentParents()),
                          entry(UserInfo.class, userInfo()));

    @Override
    public boolean canProvideFor(TypeUsage targetType) {
        return MAPPINGS.keySet().stream().anyMatch(targetType::isOfType);
    }

    @Override
    public Set<Arbitrary<?>> provideFor(TypeUsage targetType,
                                        SubtypeProvider subtypeProvider) {
        return Set.of(MAPPINGS.get(targetType.getRawType()));
    }

    private static <K, V> AbstractMap.SimpleImmutableEntry<K, V> entry(K key,
                                                                       V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }
}