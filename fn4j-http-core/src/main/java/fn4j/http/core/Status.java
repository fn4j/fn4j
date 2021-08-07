package fn4j.http.core;

import io.vavr.collection.LinkedHashSet;

public record Status(StatusCode statusCode,
                     ReasonPhrase reasonPhrase) {
    public static final Status OK = new Status(new StatusCode(200),
                                               new ReasonPhrase("OK"));
    public static final Status NOT_FOUND = new Status(new StatusCode(404),
                                                      new ReasonPhrase("NOT FOUND"));
    public static final Status METHOD_NOT_ALLOWED = new Status(new StatusCode(405),
                                                               new ReasonPhrase("METHOD NOT ALLOWED"));
    public static final Status INTERNAL_SERVER_ERROR = new Status(new StatusCode(500),
                                                                  new ReasonPhrase("INTERNAL SERVER ERROR"));

    public static final LinkedHashSet<Status> COMMON_STATUSES =
            LinkedHashSet.of(OK,
                             NOT_FOUND,
                             METHOD_NOT_ALLOWED,
                             INTERNAL_SERVER_ERROR);
}