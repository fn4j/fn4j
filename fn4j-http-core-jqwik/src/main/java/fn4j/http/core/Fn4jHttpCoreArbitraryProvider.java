package fn4j.http.core;

import fn4j.http.core.header.HeaderName;
import fn4j.http.core.header.HeaderValue;
import fn4j.http.core.header.Headers;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static fn4j.http.core.Fn4jHttpCoreArbitraries.*;

public class Fn4jHttpCoreArbitraryProvider implements ArbitraryProvider {
    private static final Map<Class<?>, Function<TypeUsage, Arbitrary<?>>> MAPPINGS =
            Map.ofEntries(entry(Body.class, targetType -> bodies(targetType.getTypeArgument(0).getRawType())),
                          entry(Head.class, __ -> heads()),
                          entry(HeaderName.class, __ -> headerNames()),
                          entry(Headers.class, __ -> headers()),
                          entry(HeaderValue.class, __ -> headerValues()),
                          entry(Message.class, targetType -> messages(targetType.getTypeArgument(0).getRawType())),
                          entry(Method.class, __ -> methods()),
                          entry(ReasonPhrase.class, __ -> reasonPhrases()),
                          entry(Request.class, targetType -> requests(targetType.getTypeArgument(0).getRawType())),
                          entry(RequestHead.class, __ -> requestHeads()),
                          entry(Response.class, targetType -> responses(targetType.getTypeArgument(0).getRawType())),
                          entry(ResponseHead.class, __ -> responseHeads()),
                          entry(Status.class, __ -> statuses()),
                          entry(StatusCode.class, __ -> statusCodes()));

    @Override
    public boolean canProvideFor(TypeUsage targetType) {
        return MAPPINGS.keySet().stream().anyMatch(targetType::isOfType);
    }

    @Override
    public Set<Arbitrary<?>> provideFor(TypeUsage targetType,
                                        SubtypeProvider subtypeProvider) {
        return Set.of(MAPPINGS.get(targetType.getRawType()).apply(targetType));
    }

    private static <K, V> AbstractMap.SimpleImmutableEntry<K, V> entry(K key,
                                                                       V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }
}