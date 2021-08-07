package fn4j.http.core;

import io.vavr.control.Option;

public record Body<B>(B value) {
    public static <B> Option<Body<B>> maybeBody(B value) {
        return Option.of(value).map(Body::new);
    }
}