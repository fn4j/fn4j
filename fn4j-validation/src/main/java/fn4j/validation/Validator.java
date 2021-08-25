package fn4j.validation;

@FunctionalInterface
public interface Validator<A> {
    Validation<A> validate(A value);

    static <A> Validator<A> valid() {
        return Valid::valid;
    }
}