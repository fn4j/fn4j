package fn4j.net.uri;

public record Query(String value) implements UriComponent {
    @Override
    public String encode() {
        return value;
    }
}