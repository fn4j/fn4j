package fn4j.validation;

public interface ValidationCursor<A> {
    A value();

    record Root<A>(A value) implements ValidationCursor<A> {
    }
}