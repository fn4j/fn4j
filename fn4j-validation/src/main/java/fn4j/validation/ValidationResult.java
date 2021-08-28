package fn4j.validation;

import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Either;

import java.util.function.Function;

public interface ValidationResult<A> extends Iterable<A> {
    Either<Invalid<A>, Valid<A>> toEither();

    Either<? extends Seq<? extends Violation>, A> toValuesEither();

    <B> ValidationResult<B> map(Function<? super A, ? extends B> mapper);

    ValidationResult<A> mapInvalid(Function<Invalid<A>, Invalid<A>> mapper);

    <B> ValidationResult<B> flatMap(Function<? super A, ? extends ValidationResult<B>> mapper);

    <B> B fold(Function<Invalid<A>, ? extends B> invalidMapper,
               Function<Valid<A>, ? extends B> validMapper);

    static <A> ValidationResult<A> valid(A value) {
        return new Valid<>(value);
    }

    static <A> ValidationResult<A> invalid(Violation... violations) {
        return new Invalid<>(Array.of(violations));
    }

    static <A> ValidationResult<A> invalid(Iterable<? extends Violation> violations) {
        return new Invalid<>(Array.ofAll(violations));
    }

    static <A> ValidationResult<A> ofAll(A value,
                                         Iterable<? extends ValidationResult<?>> validations) {
        return Stream.ofAll(validations)
                     .foldLeft(valid(value),
                               (accumulatedValidation, currentValidation) -> currentValidation.fold(currentInvalid -> accumulatedValidation.fold(accumulatedInvalid -> invalid(accumulatedInvalid.violations()
                                                                                                                                                                                                 .appendAll(currentInvalid.violations())),
                                                                                                                                                 __ -> invalid(currentInvalid.violations())),
                                                                                                    __ -> accumulatedValidation));
    }
}