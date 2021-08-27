package fn4j.validation;

import io.vavr.collection.Seq;
import io.vavr.control.Either;

import java.util.function.Function;

public record Valid<A>(A value) implements Validation<A> {
    @Override
    public Either<? extends Seq<Violation>, A> toEither() {
        return Either.right(value);
    }

    @Override
    public <B> Validation<B> map(Function<? super A, ? extends B> mapper) {
        return new Valid<>(mapper.apply(value));
    }

    @Override
    public <B> Validation<B> flatMap(Function<? super A, ? extends Validation<B>> mapper) {
        return mapper.apply(value);
    }
}