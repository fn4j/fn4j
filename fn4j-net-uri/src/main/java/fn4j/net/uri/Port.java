package fn4j.net.uri;

public record Port(int value) implements UriComponent {
    @Override
    public String encode() {
        return String.valueOf(value);
    }
}