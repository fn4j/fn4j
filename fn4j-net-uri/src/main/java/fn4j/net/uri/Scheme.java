package fn4j.net.uri;

public record Scheme(String value) implements UriComponent {
    public static final String HTTP_VALUE = "http";
    public static final Scheme HTTP = new Scheme(HTTP_VALUE);
    public static final String HTTPS_VALUE = "https";
    public static final Scheme HTTPS = new Scheme(HTTPS_VALUE);

    @Override
    public String encode() {
        return value;
    }
}