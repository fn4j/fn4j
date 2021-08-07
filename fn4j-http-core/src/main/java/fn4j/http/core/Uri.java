package fn4j.http.core;

import java.net.URI;

public record Uri(URI value) {

    /**
     * @throws IllegalArgumentException If the given string violates RFC&nbsp;2396
     * @see URI#create(String)
     */
    public Uri(String value) {
        this(URI.create(value));
    }

    public Path path() {
        return new Path(value.getPath());
    }
}