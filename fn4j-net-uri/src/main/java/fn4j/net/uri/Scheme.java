package fn4j.net.uri;

public record Scheme(String value) implements UriComponent {
    @Override
    public String encode() {
        return value;
    }
}