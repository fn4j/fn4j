package fn4j.http.core;

import io.vavr.collection.LinkedHashSet;

public record Status(StatusCode statusCode,
                     ReasonPhrase reasonPhrase) {

    public Status(int statusCode,
                  String reasonPhrase) {
        this(new StatusCode(statusCode),
             new ReasonPhrase(reasonPhrase));
    }

    public Status(StatusCode statusCode) {
        this(statusCode, COMMON_STATUSES.find(status -> status.statusCode()
                                                              .equals(statusCode))
                                        .map(Status::reasonPhrase)
                                        .getOrElse(new ReasonPhrase("UNCOMMON")));
    }

    public Status(int statusCode) {
        this(new StatusCode(statusCode));
    }

    public static final Status OK = new Status(StatusCode.OK, ReasonPhrase.OK);
    public static final Status CREATED = new Status(StatusCode.CREATED, ReasonPhrase.CREATED);
    public static final Status NO_CONTENT = new Status(StatusCode.NO_CONTENT, ReasonPhrase.NO_CONTENT);
    public static final Status BAD_REQUEST = new Status(StatusCode.BAD_REQUEST, ReasonPhrase.BAD_REQUEST);
    public static final Status UNAUTHORIZED = new Status(StatusCode.UNAUTHORIZED, ReasonPhrase.UNAUTHORIZED);
    public static final Status FORBIDDEN = new Status(StatusCode.FORBIDDEN, ReasonPhrase.FORBIDDEN);
    public static final Status NOT_FOUND = new Status(StatusCode.NOT_FOUND, ReasonPhrase.NOT_FOUND);
    public static final Status METHOD_NOT_ALLOWED = new Status(StatusCode.METHOD_NOT_ALLOWED, ReasonPhrase.METHOD_NOT_ALLOWED);
    public static final Status INTERNAL_SERVER_ERROR = new Status(StatusCode.INTERNAL_SERVER_ERROR, ReasonPhrase.INTERNAL_SERVER_ERROR);

    public static final LinkedHashSet<Status> COMMON_STATUSES = LinkedHashSet.of(OK,
                                                                                 NOT_FOUND,
                                                                                 METHOD_NOT_ALLOWED,
                                                                                 INTERNAL_SERVER_ERROR);
}