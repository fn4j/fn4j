package fn4j.validation;

public interface Movement {
    Name name();

    static Movement movement(Name name) {
        return new Immutable(name);
    }

    record Immutable(Name name) implements Movement {
    }

    record Name(String value) {
    }
}