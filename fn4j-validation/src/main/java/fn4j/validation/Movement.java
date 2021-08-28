package fn4j.validation;

public record Movement(String value) {

    static Movement movement(String value) {
        return new Movement(value);
    }
}