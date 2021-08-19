package fn4j.http.core;

public record StatusCode(int value) {
    public static final int OK_VALUE = 200;
    public static final StatusCode OK = new StatusCode(OK_VALUE);

    public static final int CREATED_VALUE = 201;
    public static final StatusCode CREATED = new StatusCode(CREATED_VALUE);

    public static final int NO_CONTENT_VALUE = 204;
    public static final StatusCode NO_CONTENT = new StatusCode(NO_CONTENT_VALUE);

    public static final int BAD_REQUEST_VALUE = 400;
    public static final StatusCode BAD_REQUEST = new StatusCode(BAD_REQUEST_VALUE);

    public static final int UNAUTHORIZED_VALUE = 401;
    public static final StatusCode UNAUTHORIZED = new StatusCode(UNAUTHORIZED_VALUE);

    public static final int FORBIDDEN_VALUE = 403;
    public static final StatusCode FORBIDDEN = new StatusCode(FORBIDDEN_VALUE);

    public static final int NOT_FOUND_VALUE = 404;
    public static final StatusCode NOT_FOUND = new StatusCode(NOT_FOUND_VALUE);

    public static final int METHOD_NOT_ALLOWED_VALUE = 405;
    public static final StatusCode METHOD_NOT_ALLOWED = new StatusCode(METHOD_NOT_ALLOWED_VALUE);

    public static final int INTERNAL_SERVER_ERROR_VALUE = 500;
    public static final StatusCode INTERNAL_SERVER_ERROR = new StatusCode(INTERNAL_SERVER_ERROR_VALUE);
}