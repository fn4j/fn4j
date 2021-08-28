package fn4j.validation;

import io.vavr.Function1;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

import java.util.function.Function;

import static fn4j.validation.Movement.movement;
import static fn4j.validation.Movement.name;

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

    default <C> Validator<C, B> move(String movement,
                                     Function<? super C, ? extends A> mover) {
        return move(movement(name(movement)), mover);
    }

    default <C> Validator<C, B> move(Movement movement,
                                     Function<? super C, ? extends A> mover) {
        return ((Validator<C, B>) cursor -> validate(mover.apply(cursor.value()))).registerManualCursorMovement(movement);
    }
}