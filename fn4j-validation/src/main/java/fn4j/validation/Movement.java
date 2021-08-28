package fn4j.validation;

public interface Movement {
    Name name();

    static Movement movement(Name name) {
        return new Immutable(name);
    }

    static Name name(String value) {
        return new Name(value);
    }

    record Immutable(Name name) implements Movement {
    }

    record Name(String value) {
    }
}