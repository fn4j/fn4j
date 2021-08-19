package fn4j.http.core;

public record HeaderName(String value) {
    public static final String ACCEPT_VALUE = "Accept";
    public static final HeaderName ACCEPT = new HeaderName(ACCEPT_VALUE);
    public static final String ALLOW_VALUE = "Allow";
    public static final HeaderName ALLOW = new HeaderName(ALLOW_VALUE);
    public static final String AUTHENTICATION_VALUE = "Authentication";
    public static final HeaderName AUTHENTICATION = new HeaderName(AUTHENTICATION_VALUE);
    public static final String CONTENT_TYPE_VALUE = "Content-Type";
    public static final HeaderName CONTENT_TYPE = new HeaderName(CONTENT_TYPE_VALUE);
    public static final String LOCATION_VALUE = "Location";
    public static final HeaderName LOCATION = new HeaderName(LOCATION_VALUE);
}