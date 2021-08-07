package fn4j.http.core;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.providers.ArbitraryProvider;
import net.jqwik.api.providers.TypeUsage;

import java.util.Set;

import static fn4j.http.core.Fn4jHttpCoreArbitraries.headerNames;

public class HeaderNameArbitraryProvider implements ArbitraryProvider {
    @Override
    public boolean canProvideFor(TypeUsage targetName) {
        return targetName.isOfType(HeaderName.class);
    }

    @Override
    public Set<Arbitrary<?>> provideFor(TypeUsage targetName,
                                        SubtypeProvider subtypeProvider) {
        return Set.of(headerNames());
    }
}