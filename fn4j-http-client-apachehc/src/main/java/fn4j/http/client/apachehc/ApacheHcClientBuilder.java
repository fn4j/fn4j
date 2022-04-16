package fn4j.http.client.apachehc;

import fn4j.http.client.ClientBuilder;
import fn4j.http.client.RequestTimeout;
import io.vavr.control.Option;

public class ApacheHcClientBuilder implements ClientBuilder<ApacheHcClient, ApacheHcClientBuilder> {
    private Option<RequestTimeout> maybeTimeout = Option.none();

    @Override
    public ApacheHcClientBuilder maybeRequestTimeout(Option<RequestTimeout> maybeRequestTimeout) {
        this.maybeTimeout = maybeRequestTimeout;
        return this;
    }

    @Override
    public ApacheHcClient build() {
        return new ApacheHcClient(maybeTimeout);
    }
}