package fn4j.validation;

public interface Violation {
    static Violation violation() {
        return new Violation() {
        };
    }
}