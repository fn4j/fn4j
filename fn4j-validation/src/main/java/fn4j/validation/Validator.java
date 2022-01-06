package fn4j.validation;

import io.vavr.Function1;
import io.vavr.collection.Stream;

import java.util.function.Function;

@FunctionalInterface
public interface Validator<A, B> extends Function1<A, Validated<B>> {
    @Override
    Validated<B> apply(A value);

    @Override
    default <C> Validator<C, B> compose(Function<? super C, ? extends A> mapper) {
        return value -> apply(mapper.apply(value));
    }

    default <C> Validator<A, C> mapValid(Function<? super B, ? extends C> mapper) {
        return value -> apply(value).map(mapper);
    }

    default Validator<A, B> mapInvalid(Function<Invalid<B>, Invalid<B>> mapper) {
        return value -> apply(value).mapInvalid(mapper);
    }

    default <C> Validator<A, C> and(Validator<? super B, C> mapper) {
        return value -> apply(value).flatMap(mapper);
    }

    default Validator<A, A> inputAsOutput() {
        return value -> mapValid(__ -> value).apply(value);
    }

    default Validator<A, B> withName(String name) {
        return mapInvalid(invalid -> {
            return invalid.mapViolations(violations -> {
                return violations.map(violation -> {
                    return violation.mapPath(elements -> elements.prepend(name));
                });
            });
        });
    }

    default Validator<A, B> withMessage(Function<A, String> message) {
        return value -> apply(value).mapInvalid(invalid -> {
            return invalid.mapViolations(violations -> {
                return violations.map(violation -> {
                    return violation.withMessage(message.apply(value));
                });
            });
        });
    }

    static <A, B> Validator<A, B> of(Function<? super A, ? extends Validated<B>> validator) {
        return validator::apply;
    }

    @SafeVarargs
    static <A> Validator<A, A> ofAll(Validator<A, ?> validator,
                                     Validator<A, ?>... validators) {
        return Validator.ofAll(Stream.of(validators).prepend(validator));
    }

    static <A> Validator<A, A> ofAll(Iterable<? extends Validator<A, ?>> validators) {
        return value -> Validated.ofAll(value, Stream.ofAll(validators).map(validator -> validator.apply(value)));
    }
}