package fn4j.net.uri;

public record UserInfo(String value) implements UriComponent {
    @Override
    public String encode() {
        return value;
    }
}