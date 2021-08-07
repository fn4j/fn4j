package fn4j.http.core;

public record HeaderName(String value) {
    public static final HeaderName CONTENT_TYPE = new HeaderName("Content-Type");
    public static final HeaderName ALLOW = new HeaderName("Allow");
}