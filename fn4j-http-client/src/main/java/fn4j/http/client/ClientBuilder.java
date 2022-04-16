package fn4j.http.client;

import io.vavr.control.Option;

public interface ClientBuilder<C extends Client, SELF extends ClientBuilder<C, SELF>> {
    default SELF requestTimeout(RequestTimeout requestTimeout) {
        return maybeRequestTimeout(Option.of(requestTimeout));
    }

    default SELF noRequestTimeout() {
        return maybeRequestTimeout(Option.none());
    }

    SELF maybeRequestTimeout(Option<RequestTimeout> maybeRequestTimeout);

    C build();
}