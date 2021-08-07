package fn4j.http.core;

public record HeaderName(String value) {
    public static final String CONTENT_TYPE_VALUE = "Content-Type";
    public static final HeaderName CONTENT_TYPE = new HeaderName(CONTENT_TYPE_VALUE);
    public static final String ALLOW_VALUE = "Allow";
    public static final HeaderName ALLOW = new HeaderName(ALLOW_VALUE);
}