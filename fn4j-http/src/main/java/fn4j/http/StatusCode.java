package fn4j.http;

public record StatusCode(int value) {
    public static final StatusCode OK = new StatusCode(200);
}