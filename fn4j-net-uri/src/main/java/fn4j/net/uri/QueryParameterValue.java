package fn4j.net.uri;

import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

public record QueryParameterValue(String value) implements UriComponent {
    @Override
    public String encode() {
        return URLEncoder.encode(value, UTF_8);
    }
}