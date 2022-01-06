package fn4j.validation;

import io.vavr.collection.Seq;
import io.vavr.control.Either;

import java.util.Iterator;
import java.util.function.Function;

public record Invalid<A>(Seq<Violation> violations) implements Validated<A> {
    public Invalid<A> mapViolations(Function<? super Seq<Violation>, ? extends Seq<Violation>> mapper) {
        return new Invalid<>(mapper.apply(violations));
    }

    @Override
    public Either<Invalid<A>, Valid<A>> toEither() {
        return Either.left(this);
    }

    @Override
    public Either<Seq<Violation>, A> toValuesEither() {
        return Either.left(violations);
    }

    @Override
    public <B> Validated<B> map(Function<? super A, ? extends B> mapper) {
        return new Invalid<>(violations);
    }

    @Override
    public Validated<A> mapInvalid(Function<Invalid<A>, Invalid<A>> mapper) {
        return mapper.apply(this);
    }

    @Override
    public <B> Validated<B> flatMap(Function<? super A, ? extends Validated<B>> mapper) {
        return new Invalid<>(violations);
    }

    @Override
    public <B> B fold(Function<Invalid<A>, ? extends B> invalidMapper,
                      Function<Valid<A>, ? extends B> validMapper) {
        return invalidMapper.apply(this);
    }

    @Override
    public Iterator<A> iterator() {
        return io.vavr.collection.Iterator.empty();
    }
}