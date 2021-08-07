package fn4j.http.core;

import io.vavr.collection.LinkedHashSet;

public record Method(String value) {
    public static final String GET_VALUE = "GET";
    public static final Method GET = new Method(GET_VALUE);
    public static final String HEAD_VALUE = "HEAD";
    public static final Method HEAD = new Method(HEAD_VALUE);
    public static final String POST_VALUE = "POST";
    public static final Method POST = new Method(POST_VALUE);
    public static final String PUT_VALUE = "PUT";
    public static final Method PUT = new Method(PUT_VALUE);
    public static final String DELETE_VALUE = "DELETE";
    public static final Method DELETE = new Method(DELETE_VALUE);
    public static final String CONNECT_VALUE = "CONNECT";
    public static final Method CONNECT = new Method(CONNECT_VALUE);
    public static final String OPTIONS_VALUE = "OPTIONS";
    public static final Method OPTIONS = new Method(OPTIONS_VALUE);
    public static final String TRACE_VALUE = "TRACE";
    public static final Method TRACE = new Method(TRACE_VALUE);
    public static final String PATCH_VALUE = "PATCH";
    public static final Method PATCH = new Method(PATCH_VALUE);

    /**
     * Common and standardized HTTP methods as per https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods
     */
    public static final LinkedHashSet<Method> COMMON_METHODS =
            LinkedHashSet.of(GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH);
}