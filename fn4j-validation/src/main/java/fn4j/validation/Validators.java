package fn4j.validation;

import static fn4j.validation.Validation.invalid;
import static fn4j.validation.Validation.valid;
import static fn4j.validation.Violation.violation;

public interface Validators {
    static <A> Validator<A, A> notNull() {
        return value -> value != null ? valid(value) : invalid(violation());
    }

    static Validator<String, String> notEmpty() {
        return Validators.<String>notNull().mapValidation(value -> !value.isEmpty() ? Validation.valid(value) : invalid(violation()));
    }
}