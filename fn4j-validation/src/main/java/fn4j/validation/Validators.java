package fn4j.validation;

import io.vavr.collection.Stream;

import static fn4j.validation.Validation.invalid;
import static fn4j.validation.Validation.valid;
import static fn4j.validation.Violation.violation;

public interface Validators {
    static <A> Validator<A, A> notNull() {
        return value -> value != null ? valid(value) : invalid(violation());
    }

    interface Iterables {
        static <A extends Iterable<?>> Validator<A, A> notEmpty() {
            return Validators.<A>notNull().mapValidation(iterable -> iterable.iterator().hasNext() ? valid(iterable) : invalid(violation()));
        }

        static <I extends Iterable<A>, A> Validator<I, I> each(Validator<A, A> elementValidator) {
            return Validators.<I>notNull().mapValidation(iterable -> {
                return Stream.ofAll(iterable)
                             .zipWithIndex()
                             .map(elementAndIndex -> {
                                 var element = elementAndIndex._1();
                                 var index = elementAndIndex._2(); // TODO: Use index in message / cursor
                                 return elementValidator.apply(element).mapInvalid(invalid -> new Invalid<>(invalid.violations()));
                             })
                             .foldLeft(valid(iterable), (acc, cur) -> cur.fold(curInvalid -> acc.fold(accInvalid -> invalid(accInvalid.violations()
                                                                                                                                      .appendAll(curInvalid.violations())),
                                                                                                      __ -> invalid(curInvalid.violations())),
                                                                               __ -> acc));
            });
        }
    }

    interface Strings {
        static Validator<String, String> notBlank() {
            return Validators.<String>notNull().mapValidation(value -> !value.trim().isEmpty() ? Validation.valid(value) : invalid(violation()));
        }
    }

    interface Integers {
        static Validator<Integer, Integer> greaterThanOrEqualTo(int other) {
            return Validators.<Integer>notNull().mapValidation(value -> value >= other ? valid(value) : invalid(violation()));
        }
    }
}