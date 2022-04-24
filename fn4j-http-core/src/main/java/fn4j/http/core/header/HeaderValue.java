package fn4j.http.core.header;

public record HeaderValue(String value) {
    public HeaderValue(String value) {
        this.value = value != null ? value.trim() : null;
    }
}