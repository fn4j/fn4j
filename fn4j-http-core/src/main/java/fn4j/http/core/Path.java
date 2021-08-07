package fn4j.http.core;

public record Path(String value) {
    public static final String ROOT_VALUE = "/";
    public static final Path ROOT = new Path(ROOT_VALUE);
}