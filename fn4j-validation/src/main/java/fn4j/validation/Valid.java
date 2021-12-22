package fn4j.validation;

import io.vavr.collection.Seq;
import io.vavr.control.Either;

import java.util.Iterator;
import java.util.function.Function;

public record Valid<A>(A value) implements ValidationResult<A> {
    @Override
    public Either<Invalid<A>, Valid<A>> toEither() {
        return Either.right(this);
    }

    @Override
    public Either<Seq<Violation>, A> toValuesEither() {
        return Either.right(value);
    }

    @Override
    public <B> ValidationResult<B> map(Function<? super A, ? extends B> mapper) {
        return new Valid<>(mapper.apply(value));
    }

    @Override
    public ValidationResult<A> mapInvalid(Function<Invalid<A>, Invalid<A>> mapper) {
        return this;
    }

    @Override
    public <B> ValidationResult<B> flatMap(Function<? super A, ? extends ValidationResult<B>> mapper) {
        return mapper.apply(value);
    }

    @Override
    public <B> B fold(Function<Invalid<A>, ? extends B> invalidMapper,
                      Function<Valid<A>, ? extends B> validMapper) {
        return validMapper.apply(this);
    }

    @Override
    public Iterator<A> iterator() {
        return io.vavr.collection.Iterator.of(value);
    }
}