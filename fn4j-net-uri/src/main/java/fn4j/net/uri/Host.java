package fn4j.net.uri;

public record Host(String value) implements UriComponent {
    @Override
    public String encode() {
        return value;
    }
}