package fn4j.validation;

import io.vavr.collection.Stream;

import java.util.UUID;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static fn4j.validation.ValidationResult.invalid;
import static fn4j.validation.ValidationResult.valid;
import static fn4j.validation.Violation.key;
import static fn4j.validation.Violation.violation;

public interface Validators {
    static <A> Validator<A, A> notNull() {
        return value -> value != null ? valid(value) : invalid(violation(key("fn4j.validation.Validators.notNull")));
    }

    interface Iterables {
        static <A extends Iterable<?>> Validator<A, A> notEmpty() {
            return Validators.<A>notNull().map(iterable -> iterable.iterator().hasNext() ? valid(iterable) : invalid(violation(key("fn4j.validation.Validators.Iterables.notEmpty"))));
        }

        static <I extends Iterable<A>, A> Validator<I, I> each(Validator<A, A> elementValidator) {
            return Validators.<I>notNull().map(iterable -> ValidationResult.ofAll(iterable, Stream.ofAll(iterable).zipWithIndex().map(elementAndIndex -> {
                var element = elementAndIndex._1();
                var index = elementAndIndex._2();
                return elementValidator.withName("[" + index + ']').apply(element);
            })));
        }
    }

    interface Strings {
        static Validator<String, String> notEmpty() {
            return Validators.<String>notNull().map(value -> !value.isEmpty() ? valid(value) : invalid(violation(key("fn4j.validation.Validators.Strings.notEmpty"))));
        }

        static Validator<String, String> notBlank() {
            return Validators.<String>notNull().map(value -> !value.strip().isEmpty() ? valid(value) : invalid(violation(key("fn4j.validation.Validators.Strings.notBlank"))));
        }

        static Validator<String, MatchResult> pattern(String pattern) {
            return pattern(Pattern.compile(pattern));
        }

        static Validator<String, MatchResult> pattern(Pattern pattern) {
            return string -> {
                var matcher = pattern.matcher(string);
                return matcher.matches() ? valid(matcher.toMatchResult()) : invalid(violation(key("fn4j.validation.Validators.Strings.match")));
            };
        }
    }

    interface Integers {
        static Validator<Integer, Integer> min(int other) {
            return Validators.<Integer>notNull().map(value -> value >= other ? valid(value) : invalid(violation(key("fn4j.validation.Validators.Integers.greaterThanOrEqualTo"))));
        }
    }

    interface Uuids {
        static Validator<String, UUID> uuidFromString() {
            return Validators.<String>notNull().map(string -> {
                try {
                    return valid(UUID.fromString(string));
                } catch (IllegalArgumentException e) {
                    return invalid(violation(key("fn4j.validation.Validators.Uuids.uuid"), e));
                }
            });
        }
    }
}