package fn4j.validation;

import io.vavr.collection.Seq;
import io.vavr.control.Either;

public record Invalid<A>(Seq<Violation> violations) implements Validation<A> {
    @Override
    public Either<? extends Seq<Violation>, A> toEither() {
        return Either.left(violations);
    }
}