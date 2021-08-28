package fn4j.validation;

import io.vavr.Function1;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

import java.util.function.Function;

import static fn4j.validation.Movement.movement;

@FunctionalInterface
public interface Validator<A, B> extends Function1<A, Validation<B>> {
    @Override
    Validation<B> apply(A value);

    default Validation<B> validate(A value) {
        return apply(value);
    }

    default <C> Validator<A, C> map(Function<? super B, ? extends Validation<C>> mapper) {
        return value -> apply(value).flatMap(mapper);
    }

    default Validator<A, B> manualMove(Movement... movements) {
        return manualMove(Stream.of(movements));
    }

    default Validator<A, B> manualMove(Seq<Movement> movements) {
        return value -> apply(value).mapInvalid(invalid -> {
            return new Invalid<>(invalid.violations().map(violation -> {
                return violation.mapMovements(followingMovements -> {
                    return followingMovements.appendAll(movements);
                });
            }));
        });
    }

    default <C> Validator<C, B> move(String movement,
                                     Function<? super C, ? extends A> mover) {
        return move(movement(movement), mover);
    }

    default <C> Validator<C, B> move(Movement movement,
                                     Function<? super C, ? extends A> mover) {
        return ((Validator<C, B>) value -> validate(mover.apply(value))).manualMove(movement);
    }

    @SafeVarargs
    static <A, B> Validator<A, A> ofAll(Validator<A, B> validator,
                                        Validator<A, ?>... validators) {
        return Validator.ofAll(Array.of(validators).prepend(validator));
    }

    static <A> Validator<A, A> ofAll(Iterable<? extends Validator<A, ?>> validators) {
        return value -> Validation.ofAll(value, Stream.ofAll(validators).map(validator -> validator.validate(value)));
    }
}