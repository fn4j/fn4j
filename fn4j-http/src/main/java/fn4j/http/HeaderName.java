package fn4j.http;

public record HeaderName(String value) {
    public static final HeaderName CONTENT_TYPE = new HeaderName("content-type");
}