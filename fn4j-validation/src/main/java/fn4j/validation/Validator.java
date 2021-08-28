package fn4j.validation;

import io.vavr.Function1;
import io.vavr.collection.Array;
import io.vavr.collection.Stream;

import java.util.function.Function;

@FunctionalInterface
public interface Validator<A, B> extends Function1<A, Validation<B>> {
    @Override
    Validation<B> apply(A value);

    default <C> Validator<A, C> map(Function<? super B, ? extends Validation<C>> mapper) {
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

    default <C> Validator<C, B> as(String name,
                                   Function<? super C, ? extends A> extractor) {
        return ((Validator<C, B>) value -> apply(extractor.apply(value))).withName(name);
    }

    @SafeVarargs
    static <A, B> Validator<A, A> ofAll(Validator<A, B> validator,
                                        Validator<A, ?>... validators) {
        return Validator.ofAll(Array.of(validators).prepend(validator));
    }

    static <A> Validator<A, A> ofAll(Iterable<? extends Validator<A, ?>> validators) {
        return value -> Validation.ofAll(value, Stream.ofAll(validators).map(validator -> validator.apply(value)));
    }
}