package fn4j.http.core;

import io.vavr.collection.LinkedHashSet;

public record StatusCode(int value) {
    public static final StatusCode OK = new StatusCode(200);
    public static final StatusCode NOT_FOUND = new StatusCode(404);
    public static final StatusCode METHOD_NOT_ALLOWED = new StatusCode(405);

    public static final LinkedHashSet<StatusCode> COMMON_STATUS_CODES =
            LinkedHashSet.of(OK, NOT_FOUND, METHOD_NOT_ALLOWED);
}