package fn4j.validation;

import io.vavr.collection.Array;
import io.vavr.control.Either;

import java.util.function.Function;

public interface Validation<A> extends Iterable<A> {
    Either<Invalid<A>, Valid<A>> toEither();

    <B> Validation<B> map(Function<? super A, ? extends B> mapper);

    Validation<A> mapInvalid(Function<Invalid<A>, Invalid<A>> mapper);

    <B> Validation<B> flatMap(Function<? super A, ? extends Validation<B>> mapper);

    <B> B fold(Function<Invalid<A>, ? extends B> invalidMapper,
               Function<Valid<A>, ? extends B> validMapper);

    static <A> Validation<A> valid(A value) {
        return new Valid<>(value);
    }

    static <A> Validation<A> invalid(Violation... violations) {
        return new Invalid<>(Array.of(violations));
    }

    static <A> Validation<A> invalid(Iterable<? extends Violation> violations) {
        return new Invalid<>(Array.ofAll(violations));
    }
}