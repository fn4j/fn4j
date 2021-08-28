package fn4j.validation;

public interface Movement {
    Name name();

    record Name(String value) {
    }
}