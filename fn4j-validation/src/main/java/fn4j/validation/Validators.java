package fn4j.validation;

import io.vavr.collection.Stream;

import static fn4j.validation.Movement.movement;
import static fn4j.validation.Movement.name;
import static fn4j.validation.Validation.invalid;
import static fn4j.validation.Validation.valid;
import static fn4j.validation.Violation.key;
import static fn4j.validation.Violation.violation;

public interface Validators {
    static <A> Validator<A, A> notNull() {
        return value -> value != null ? valid(value) : invalid(violation(key("fn4j.validation.Validators.notNull")));
    }

    interface Iterables {
        static <A extends Iterable<?>> Validator<A, A> notEmpty() {
            return Validators.<A>notNull().mapValidation(iterable -> iterable.iterator().hasNext() ? valid(iterable) : invalid(violation(key("fn4j.validation.Validators.Iterables.notEmpty"))));
        }

        static <I extends Iterable<A>, A> Validator<I, I> each(Validator<A, A> elementValidator) {
            return Validators.<I>notNull().mapValidation(iterable -> Validation.ofAll(iterable, Stream.ofAll(iterable).zipWithIndex().map(elementAndIndex -> {
                var element = elementAndIndex._1();
                var index = elementAndIndex._2();
                return elementValidator.registerManualMovement(movement(name("[" + index + ']')))
                                       .validate(element);
            })));
        }
    }

    interface Strings {
        static Validator<String, String> notBlank() {
            return Validators.<String>notNull().mapValidation(value -> !value.trim().isEmpty() ? valid(value) : invalid(violation(key("fn4j.validation.Validators.Strings.notBlank"))));
        }
    }

    interface Integers {
        static Validator<Integer, Integer> greaterThanOrEqualTo(int other) {
            return Validators.<Integer>notNull().mapValidation(value -> value >= other ? valid(value) : invalid(violation(key("fn4j.validation.Validators.Integers.greaterThanOrEqualTo"))));
        }
    }
}