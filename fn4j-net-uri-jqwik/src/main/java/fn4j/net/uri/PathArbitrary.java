package fn4j.net.uri;

import io.vavr.collection.Array;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.RandomDistribution;
import net.jqwik.api.arbitraries.SizableArbitrary;

import static fn4j.net.uri.Fn4jNetUriArbitraries.pathSegments;

public class PathArbitrary extends AbstractSimpleSizeableArbitrary<PathArbitrary, Path> implements SizableArbitrary<Path> {
    public PathArbitrary() {
        super(PathArbitrary::new);
    }

    @Override
    protected Arbitrary<Path> arbitrary(int minSize,
                                        int maxSize,
                                        RandomDistribution sizeDistribution) {
        return pathSegments().list()
                             .ofMinSize(minSize)
                             .ofMaxSize(maxSize)
                             .withSizeDistribution(sizeDistribution)
                             .map(pathSegments -> new Path(Array.ofAll(pathSegments)));
    }
}