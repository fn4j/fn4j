package fn4j.http.core;

import io.vavr.collection.LinkedHashSet;

public record Method(String value) {
    public static final Method GET = new Method("GET");
    public static final Method HEAD = new Method("HEAD");
    public static final Method POST = new Method("POST");
    public static final Method PUT = new Method("PUT");
    public static final Method DELETE = new Method("DELETE");
    public static final Method CONNECT = new Method("CONNECT");
    public static final Method OPTIONS = new Method("OPTIONS");
    public static final Method TRACE = new Method("TRACE");
    public static final Method PATCH = new Method("PATCH");

    /**
     * Common and standardized HTTP methods as per https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods
     */
    public static final LinkedHashSet<Method> COMMON_METHODS =
            LinkedHashSet.of(GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH);
}