package fn4j.validation;

import io.vavr.collection.Stream;

import static fn4j.validation.Movement.movement;
import static fn4j.validation.Validation.invalid;
import static fn4j.validation.Validation.valid;
import static fn4j.validation.Violation.violation;

public interface Validators {
    static <A> Validator<A, A> notNull() {
        return cursor -> cursor.value() != null ? valid(cursor.value()) : invalid(violation(new Violation.Key("fn4j.validation.Validators.notNull")));
    }

    interface Iterables {
        static <A extends Iterable<?>> Validator<A, A> notEmpty() {
            return Validators.<A>notNull().mapValidation(iterable -> iterable.iterator().hasNext() ? valid(iterable) : invalid(violation(new Violation.Key("fn4j.validation.Validators.Iterables.notEmpty"))));
        }

        static <I extends Iterable<A>, A> Validator<I, I> each(Validator<A, A> elementValidator) {
            return Validators.<I>notNull().mapValidation(iterable -> {
                return Stream.ofAll(iterable)
                             .zipWithIndex()
                             .map(elementAndIndex -> {
                                 var element = elementAndIndex._1();
                                 var index = elementAndIndex._2();
                                 return elementValidator.validate(element).mapInvalid(invalid -> {
                                     return new Invalid<>(invalid.violations().map(violation -> {
                                         return violation.mapMovements(movements -> {
                                             return movements.prepend(movement(new Movement.Name("[" + index + ']')));
                                         });
                                     }));
                                 });
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
            return Validators.<String>notNull().mapValidation(value -> !value.trim().isEmpty() ? Validation.valid(value) : invalid(violation(new Violation.Key("fn4j.validation.Validators.Strings.notBlank"))));
        }
    }

    interface Integers {
        static Validator<Integer, Integer> greaterThanOrEqualTo(int other) {
            return Validators.<Integer>notNull().mapValidation(value -> value >= other ? valid(value) : invalid(violation(new Violation.Key("fn4j.validation.Validators.Integers.greaterThanOrEqualTo"))));
        }
    }
}