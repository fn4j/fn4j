package fn4j.net.uri;

public record Host(String value) implements UriComponent {
    public static final String LOCALHOST_VALUE = "localhost";
    public static final Host LOCALHOST = new Host(LOCALHOST_VALUE);

    @Override
    public String encode() {
        return value;
    }
}