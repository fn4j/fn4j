package fn4j.http.core;

public record ReasonPhrase(String value) {
    public static final String OK_VALUE = "Ok";
    public static final ReasonPhrase OK = new ReasonPhrase(OK_VALUE);
    public static final String BAD_REQUEST_VALUE = "Bad Request";
    public static final ReasonPhrase BAD_REQUEST = new ReasonPhrase(BAD_REQUEST_VALUE);
    public static final String NOT_FOUND_VALUE = "Not Found";
    public static final ReasonPhrase NOT_FOUND = new ReasonPhrase(NOT_FOUND_VALUE);
    public static final String METHOD_NOT_ALLOWED_VALUE = "Method Not Allowed";
    public static final ReasonPhrase METHOD_NOT_ALLOWED = new ReasonPhrase(METHOD_NOT_ALLOWED_VALUE);
    public static final String INTERNAL_SERVER_ERROR_VALUE = "Internal Server Error";
    public static final ReasonPhrase INTERNAL_SERVER_ERROR = new ReasonPhrase(INTERNAL_SERVER_ERROR_VALUE);
}