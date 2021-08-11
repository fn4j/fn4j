package fn4j.net.uri;

record Literal(String value) implements UriComponent {
    static final Literal COLON = new Literal(":");
    static final Literal DOUBLE_SLASH = new Literal("//");
    static final Literal AT = new Literal("@");
    static final Literal SLASH = new Literal("/");
    static final Literal QUESTION_MARK = new Literal("?");
    static final Literal EQUALS = new Literal("=");
    static final Literal AMPERSAND = new Literal("&");
    static final Literal HASH = new Literal("#");

    @Override
    public String encode() {
        return value;
    }
}