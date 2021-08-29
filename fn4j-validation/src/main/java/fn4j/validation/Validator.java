package fn4j.validation;

import io.vavr.Function1;
import io.vavr.collection.Stream;

import java.util.function.Function;

@FunctionalInterface
public interface Validator<A, B> extends Function1<A, ValidationResult<B>> {
    @Override
    ValidationResult<B> apply(A value);

    default <C> Validator<A, C> map(Function<? super B, ? extends ValidationResult<C>> mapper) {
        return value -> apply(value).flatMap(mapper);
    }

    default Validator<A, B> mapInvalid(Function<Invalid<B>, Invalid<B>> mapper) {
        return value -> apply(value).mapInvalid(mapper);
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

    default <C> Validator<C, B> as(String name,
                                   Function<? super C, ? extends A> extractor) {
        return ((Validator<C, B>) value -> apply(extractor.apply(value))).withName(name);
    }

    static <A, B> Validator<A, B> of(Function<? super A, ? extends ValidationResult<B>> validator) {
        return validator::apply;
    }

    @SafeVarargs
    static <A> Validator<A, A> ofAll(Validator<A, ?> validator,
                                     Validator<A, ?>... validators) {
        return Validator.ofAll(Stream.of(validators).prepend(validator));
    }

    static <A> Validator<A, A> ofAll(Iterable<? extends Validator<A, ?>> validators) {
        return value -> ValidationResult.ofAll(value, Stream.ofAll(validators).map(validator -> validator.apply(value)));
    }
}