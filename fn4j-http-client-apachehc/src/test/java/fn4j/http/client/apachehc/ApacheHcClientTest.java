package fn4j.http.client.apachehc;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import fn4j.http.core.Method;
import fn4j.http.core.Request;
import fn4j.http.core.Response;
import fn4j.http.core.header.Headers;
import fn4j.net.uri.Authority;
import fn4j.net.uri.Path;
import fn4j.net.uri.Port;
import fn4j.net.uri.Uri;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.AddLifecycleHook;
import net.jqwik.api.lifecycle.PropagationMode;
import net.jqwik.api.providers.TypeUsage;
import net.jqwik.api.statistics.NumberRangeHistogram;
import net.jqwik.api.statistics.Statistics;
import net.jqwik.api.statistics.StatisticsReport;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static fn4j.http.client.apachehc.TickTock.tick;
import static fn4j.http.core.Fn4jHttpCoreArbitraries.bodies;
import static fn4j.http.core.Fn4jHttpCoreArbitraries.headers;
import static fn4j.http.core.Method.*;
import static fn4j.http.core.Request.request;
import static fn4j.net.uri.Fn4jNetUriArbitraries.uris;
import static fn4j.net.uri.Host.LOCALHOST;
import static fn4j.net.uri.Scheme.HTTP;
import static fn4j.net.uri.Scheme.HTTPS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Label("Apache HTTP Components Client")
@AddLifecycleHook(value = WireMockServerHook.class, propagateTo = PropagationMode.ALL_DESCENDANTS)
@StatisticsReport(format = NumberRangeHistogram.class)
class ApacheHcClientTest {
    @Property
    @Label("should send method")
    void shouldSendMethod(WireMockServer server,
                          @ForAll("safe") Method method) throws Exception {
        // given
        Request<byte[]> request = request(method,
                                          new Uri(server.baseUrl()),
                                          Headers.empty());

        try (var apacheHcClient = ApacheHcClient.builder().build()) {
            server.givenThat(WireMock.request(method.value(), UrlPattern.ANY).willReturn(aResponse()));

            var tick = tick();

            // when
            var eventualResponse = apacheHcClient.exchange(request);
            consume(eventualResponse, tick);

            // then no exception is thrown
        }
    }

    @Property
    @Label("should send uri")
    void shouldSendUri(WireMockServer server,
                       @ForAll Path path) throws Exception {
        // given
        var uri = new Uri(server.url(path.encode()));
        Assume.that(uri.encode().length() < 8000);
        Request<byte[]> request = request(GET,
                                          uri,
                                          Headers.empty());

        try (var apacheHcClient = ApacheHcClient.builder().build()) {
            server.givenThat(any(WireMock.urlPathEqualTo(uri.path().encode())).willReturn(aResponse()));

            var tick = tick();

            // when
            var eventualResponse = apacheHcClient.exchange(request);
            consume(eventualResponse, tick);

            // then no exception is thrown
        }
    }

    @Property(tries = 100)
    @Label("should exchange request and response")
    void shouldExchangeRequestAndResponse(WireMockServer server,
                                          @ForAll("requests") Request<byte[]> request) throws Exception {
        // given
        var requestAgainstWireMockServer = againstWireMock(server, request);
        try (var apacheHcClient = ApacheHcClient.builder().build()) {
            server.givenThat(requestMatcher(requestAgainstWireMockServer).willReturn(aResponse()));

            var tick = tick();

            // when
            var eventualResponse = apacheHcClient.exchange(requestAgainstWireMockServer);
            consume(eventualResponse, tick);

            // then no exception is thrown
        }
    }

    private static <A> Request<A> againstWireMock(WireMockServer server,
                                                  Request<A> request) {
        boolean httpsEnabled = server.getOptions().httpsSettings().enabled();
        var port = new Port(httpsEnabled ? server.httpsPort() : server.port());
        var uri = new Uri(Option.of(httpsEnabled ? HTTPS : HTTP),
                          Option.of(new Authority(Option.none(),
                                                  LOCALHOST,
                                                  Option.of(port))),
                          request.uri().path(),
                          request.uri().maybeQuery(),
                          request.uri().maybeFragment());
        return request(request.method(),
                       uri,
                       request.headers(),
                       request.maybeBody());
    }

    private MappingBuilder requestMatcher(Request<byte[]> request) {
        return WireMock.any(UrlPattern.ANY);
    }

    private void consume(Future<Response<byte[]>> eventualResponse,
                         TickTock.Tick tick) {
        eventualResponse.await(2, SECONDS);
        var tickTock = tick.tock();
        Statistics.label("duration in ms").collect(tickTock.duration());
    }

    @Provide
    Arbitrary<?> safe(TypeUsage typeUsage) {
        if (typeUsage.isOfType(Method.class)) {
            return Arbitraries.of(GET, HEAD, POST, PUT, DELETE, OPTIONS, PATCH);
        }
        return Arbitraries.forType(typeUsage.getRawType());
    }

    @Provide
    Arbitrary<Request<byte[]>> requests() {
        var methodArbitrary = Arbitraries.of(GET, HEAD, POST, PUT, DELETE, OPTIONS, PATCH);
        var uriArbitrary = uris().filter(uri -> uri.encode().length() < 8000);
        var headersArbitrary = headers().filter(headers -> headers.toString().length() < 8000);
        var maybeBodyArbitrary = bodies(byte[].class).optional().map(Option::ofOptional);

        return methodArbitrary
                .flatMap(method -> uriArbitrary
                        .flatMap(uri -> headersArbitrary
                                .flatMap(headers -> maybeBodyArbitrary
                                        .map(maybeBody -> request(method, uri, headers, maybeBody))
                                )
                        )
                );
    }
}