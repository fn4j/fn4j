package fn4j.validation;

import io.vavr.collection.Seq;
import io.vavr.control.Either;

import java.util.Iterator;
import java.util.function.Function;

public record Invalid<A>(Seq<Violation> violations) implements Validation<A> {
    @Override
    public Either<Invalid<A>, Valid<A>> toEither() {
        return Either.left(this);
    }

    @Override
    public <B> Validation<B> map(Function<? super A, ? extends B> mapper) {
        return new Invalid<>(violations);
    }

    @Override
    public Validation<A> mapInvalid(Function<Invalid<A>, Invalid<A>> mapper) {
        return mapper.apply(this);
    }

    @Override
    public <B> Validation<B> flatMap(Function<? super A, ? extends Validation<B>> mapper) {
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