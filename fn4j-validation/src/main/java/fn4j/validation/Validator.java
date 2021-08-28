package fn4j.validation;

import io.vavr.Function1;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

import java.util.function.Function;

@FunctionalInterface
public interface Validator<A, B> extends Function1<ValidationCursor<A>, Validation<B>> {
    @Override
    Validation<B> apply(ValidationCursor<A> cursor);

    default Validation<B> validate(A value) {
        return apply(new ValidationCursor.Root<>(value));
    }

    default <C> Validator<A, C> mapValidation(Function<? super B, ? extends Validation<C>> mapper) {
        return value -> apply(value).flatMap(mapper);
    }

    default Validator<A, B> registerManualCursorMovement(Movement... movements) {
        return registerManualCursorMovement(Stream.of(movements));
    }

    default Validator<A, B> registerManualCursorMovement(Seq<Movement> movements) {
        return cursor -> apply(cursor).mapInvalid(invalid -> {
            return new Invalid<>(invalid.violations().map(violation -> {
                return violation.mapMovements(followingMovements -> {
                    return followingMovements.appendAll(movements);
                });
            }));
        });
    }
}