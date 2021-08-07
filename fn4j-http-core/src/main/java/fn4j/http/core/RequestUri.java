package fn4j.http.core;

import java.net.URI;

public record RequestUri(String value) {
    public RequestPath path() {
        return new RequestPath(toJavaUri().getPath());
    }

    /**
     * @throws IllegalArgumentException If the given string violates RFC&nbsp;2396
     * @see URI#create(String)
     */
    public URI toJavaUri() {
        return URI.create(value);
    }
}