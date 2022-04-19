package fn4j.net.uri;

public record Literal(String value) implements UriComponent {
    public static final Literal COLON = new Literal(":");
    public static final Literal DOUBLE_SLASH = new Literal("//");
    public static final Literal AT = new Literal("@");
    public static final Literal SLASH = new Literal("/");
    public static final Literal QUESTION_MARK = new Literal("?");
    public static final Literal EQUALS = new Literal("=");
    public static final Literal AMPERSAND = new Literal("&");
    public static final Literal HASH = new Literal("#");

    @Override
    public String encode() {
        return value;
    }
}