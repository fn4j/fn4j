package fn4j.validation;

import fn4j.validation.Validators.Length.Exact;
import fn4j.validation.Validators.Length.Maximum;
import fn4j.validation.Validators.Length.Minimum;
import fn4j.validation.Validators.Length.Range;
import io.vavr.collection.Stream;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.LongPredicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static fn4j.validation.Validated.invalid;
import static fn4j.validation.Validated.valid;
import static fn4j.validation.Validators.Length.range;
import static fn4j.validation.Violation.key;
import static fn4j.validation.Violation.violation;

public interface Validators {
    static <A> Validator<A, A> notNull() {
        return value -> value != null ? valid(value) : invalid(violation(key("fn4j.validation.Validators.notNull")));
    }

    static <A, B> Validator<A, B> move(Function<? super A, ? extends B> mapper) {
        // TODO: Test
        return Validators.<A>notNull().mapValid(mapper);
    }

    static <A, B> Validator<A, B> move(Function<? super A, ? extends B> mapper,
                                       String name) {
        // TODO: Test
        return Validators.<A>notNull().withName(name).mapValid(mapper);
    }

    interface Iterables {
        static <A extends Iterable<?>> Validator<A, A> notEmpty() {
            return Validators.<A>notNull().flatMap(iterable -> iterable.iterator().hasNext() ? valid(iterable) : invalid(violation(key("fn4j.validation.Validators.Iterables.notEmpty"))));
        }

        static <I extends Iterable<A>, A> Validator<I, I> each(Validator<A, A> elementValidator) {
            return Validators.<I>notNull().flatMap(iterable -> Validated.ofAll(iterable, Stream.ofAll(iterable).zipWithIndex().map(elementAndIndex -> {
                var element = elementAndIndex._1();
                var index = elementAndIndex._2();
                return elementValidator.withName("[" + index + ']').apply(element);
            })));
        }
    }

    interface Strings {
        static Validator<String, String> notEmpty() {
            return Validators.<String>notNull().flatMap(value -> !value.isEmpty() ? valid(value) : invalid(violation(key("fn4j.validation.Validators.Strings.notEmpty"))));
        }

        static Validator<String, String> notBlank() {
            return Validators.<String>notNull().flatMap(value -> !value.trim().isEmpty() ? valid(value) : invalid(violation(key("fn4j.validation.Validators.Strings.notBlank"))));
        }

        static Validator<String, String> length(Exact exact) {
            return Validators.<String>notNull().flatMap(value -> exact.test(value.length()) ? valid(value) : invalid(violation(key("fn4j.validation.Validators.Strings.length.exact"))));
        }

        static Validator<String, String> length(Minimum minimum) {
            return Validators.<String>notNull().flatMap(value -> minimum.test(value.length()) ? valid(value) : invalid(violation(key("fn4j.validation.Validators.Strings.length.minimum"))));
        }

        static Validator<String, String> length(Maximum maximum) {
            return Validators.<String>notNull().flatMap(value -> maximum.test(value.length()) ? valid(value) : invalid(violation(key("fn4j.validation.Validators.Strings.length.maximum"))));
        }

        static Validator<String, String> length(Minimum minimum,
                                                Maximum maximum) {
            return length(range(minimum, maximum));
        }

        static Validator<String, String> length(Range range) {
            return Validators.<String>notNull().flatMap(value -> range.test(value.length()) ? valid(value) : invalid(violation(key("fn4j.validation.Validators.Strings.length.range"))));
        }

        static Validator<String, MatchResult> pattern(String pattern) {
            return pattern(Pattern.compile(pattern));
        }

        static Validator<String, MatchResult> pattern(Pattern pattern) {
            return Validators.<String>notNull().flatMap(string -> {
                var matcher = pattern.matcher(string);
                return matcher.matches() ? valid(matcher.toMatchResult()) : invalid(violation(key("fn4j.validation.Validators.Strings.pattern")));
            });
        }
    }

    interface Integers {
        static Validator<Integer, Integer> min(int minimum) {
            return Validators.<Integer>notNull().flatMap(value -> value >= minimum ? valid(value) : invalid(violation(key("fn4j.validation.Validators.Integers.min"))));
        }

        static Validator<Integer, Integer> max(int maximum) {
            return Validators.<Integer>notNull().flatMap(value -> value <= maximum ? valid(value) : invalid(violation(key("fn4j.validation.Validators.Integers.max"))));
        }
    }

    interface Uuids {
        static Validator<String, UUID> uuidFromString() {
            return Validators.<String>notNull().flatMap(string -> {
                try {
                    return valid(UUID.fromString(string));
                } catch (IllegalArgumentException e) {
                    return invalid(violation(key("fn4j.validation.Validators.Uuids.uuidFromString"), e));
                }
            });
        }
    }

    interface Length extends LongPredicate {
        @Override
        boolean test(long length);

        static Exact exact(long exact) {
            return new Exact(exact);
        }

        static Minimum min(long minimum) {
            return new Minimum(minimum);
        }

        static Maximum max(long maximum) {
            return new Maximum(maximum);
        }

        static Range range(long minimum,
                           long maximum) {
            return range(min(minimum), max(maximum));
        }

        static Range range(Minimum minimum,
                           Maximum maximum) {
            return new Range(minimum, maximum);
        }

        record Exact(long exact) implements Length {
            @Override
            public boolean test(long length) {
                return length == exact;
            }
        }

        record Minimum(long minimum) implements Length {
            @Override
            public boolean test(long length) {
                return length >= minimum;
            }
        }

        record Maximum(long maximum) implements Length {
            @Override
            public boolean test(long length) {
                return length <= maximum;
            }
        }

        record Range(Minimum minimum,
                     Maximum maximum) implements Length {
            @Override
            public boolean test(long length) {
                return minimum.test(length) && maximum.test(length);
            }
        }
    }
}