package fn4j.validation;

import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import io.vavr.control.Either;

import java.util.Iterator;
import java.util.function.Function;

public interface Validation<A> extends Iterable<A> {
    Either<? extends Seq<Violation>, A> toEither();

    <B> Validation<B> map(Function<? super A, ? extends B> mapper);

    <B> Validation<B> flatMap(Function<? super A, ? extends Validation<B>> mapper);

    default <B> B fold(Function<? super Seq<? extends Violation>, ? extends B> invalidMapper,
                       Function<? super A, ? extends B> validMapper) {
        return toEither().fold(invalidMapper, validMapper);
    }

    @Override
    default Iterator<A> iterator() {
        return toEither().iterator();
    }

    static <A> Validation<A> valid(A value) {
        return new Valid<>(value);
    }

    static <A> Validation<A> invalid(Violation... violations) {
        return new Invalid<>(Array.of(violations));
    }
}