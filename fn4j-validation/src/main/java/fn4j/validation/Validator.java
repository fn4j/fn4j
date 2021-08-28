package fn4j.validation;

import io.vavr.Function1;

import java.util.function.Function;

@FunctionalInterface
public interface Validator<A, B> extends Function1<ValidationCursor<A>, Validation<B>> {
    @Override
    Validation<B> apply(ValidationCursor<A> cursor);

    default Validation<B> validate(A value) {
        return apply(new ValidationCursor.Root<>(value));
    }

    default <C> Validator<A, C> map(Function<? super B, ? extends C> mapper) {
        return value -> apply(value).map(mapper);
    }

    default <C> Validator<A, C> mapValidation(Function<? super B, ? extends Validation<C>> mapper) {
        return value -> apply(value).flatMap(mapper);
    }

    default <C> Validator<A, C> flatMap(Function<? super B, ? extends Validator<B, C>> mapper) {
        return a -> apply(a).flatMap(b -> mapper.apply(b).validate(b));
    }
}