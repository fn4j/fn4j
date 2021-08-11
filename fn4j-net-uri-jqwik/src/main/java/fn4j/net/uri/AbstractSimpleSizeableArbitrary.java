package fn4j.net.uri;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.EdgeCases;
import net.jqwik.api.RandomDistribution;
import net.jqwik.api.RandomGenerator;
import net.jqwik.api.arbitraries.SizableArbitrary;

import java.util.function.Supplier;

public abstract class AbstractSimpleSizeableArbitrary<SELF extends AbstractSimpleSizeableArbitrary<SELF, U>, U> implements SizableArbitrary<U> {
    private final Supplier<SELF> selfSupplier;
    protected int minSize = 0;
    protected int maxSize = 255;
    protected RandomDistribution sizeDistribution;

    public AbstractSimpleSizeableArbitrary(Supplier<SELF> selfSupplier) {
        this.selfSupplier = selfSupplier;
    }

    protected abstract Arbitrary<U> arbitrary(int minSize,
                                              int maxSize,
                                              RandomDistribution sizeDistribution);

    @Override
    public RandomGenerator<U> generator(int genSize) {
        return arbitrary(minSize, maxSize, sizeDistribution).generator(genSize);
    }

    @Override
    public EdgeCases<U> edgeCases(int maxEdgeCases) {
        return arbitrary(minSize, maxSize, sizeDistribution).edgeCases(maxEdgeCases);
    }

    @Override
    public SELF ofMinSize(int minSize) {
        var self = selfSupplier.get();
        self.minSize = minSize;
        self.maxSize = maxSize;
        self.sizeDistribution = sizeDistribution;
        return self;
    }

    @Override
    public SELF ofMaxSize(int maxSize) {
        var self = selfSupplier.get();
        self.minSize = minSize;
        self.maxSize = maxSize;
        self.sizeDistribution = sizeDistribution;
        return self;
    }

    @Override
    public SELF withSizeDistribution(RandomDistribution sizeDistribution) {
        var self = selfSupplier.get();
        self.minSize = minSize;
        self.maxSize = maxSize;
        self.sizeDistribution = sizeDistribution;
        return self;
    }
}