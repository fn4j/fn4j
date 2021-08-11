package fn4j.net.uri;

public record Fragment(String value) implements UriComponent {
    @Override
    public String encode() {
        return value;
    }
}