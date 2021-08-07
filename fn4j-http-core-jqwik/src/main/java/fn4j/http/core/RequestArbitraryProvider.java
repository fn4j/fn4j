package fn4j.http.core;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;

import java.util.Set;

import static fn4j.http.core.Fn4jHttpCoreArbitraries.requests;

public class RequestArbitraryProvider implements ArbitraryProvider {
    @Override
    public boolean canProvideFor(TypeUsage targetType) {
        return targetType.isOfType(Request.class);
    }

    @Override
    public Set<Arbitrary<?>> provideFor(TypeUsage targetType,
                                        SubtypeProvider subtypeProvider) {
        return Set.of(requests(targetType.getTypeArgument(0).getRawType()));
    }
}