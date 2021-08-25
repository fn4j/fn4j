package fn4j.validation;

import io.vavr.collection.Seq;
import io.vavr.control.Either;

public record Valid<A>(A value) implements Validation<A> {
    @Override
    public Either<? extends Seq<Violation>, A> toEither() {
        return Either.right(value);
    }

    static <A> Valid<A> valid(A value) {
        return new Valid<>(value);
    }
}