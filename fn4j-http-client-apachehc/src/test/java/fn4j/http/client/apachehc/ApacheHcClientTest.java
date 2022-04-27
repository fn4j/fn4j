package fn4j.http.client.apachehc;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import fn4j.http.core.Method;
import fn4j.http.core.Request;
import fn4j.http.core.Response;
import fn4j.http.core.header.Headers;
import fn4j.net.uri.*;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.AddLifecycleHook;
import net.jqwik.api.lifecycle.PropagationMode;
import net.jqwik.api.providers.TypeUsage;
import net.jqwik.api.statistics.NumberRangeHistogram;
import net.jqwik.api.statistics.Statistics;
import net.jqwik.api.statistics.StatisticsReport;
import org.assertj.core.api.Assertions;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static fn4j.http.client.apachehc.ApacheHcClientTest.ArbitraryUriComponentsParent.concat;
import static fn4j.http.client.apachehc.TickTock.tick;
import static fn4j.http.core.Fn4jHttpCoreArbitraries.bodies;
import static fn4j.http.core.Fn4jHttpCoreArbitraries.headers;
import static fn4j.http.core.Method.*;
import static fn4j.http.core.Request.request;
import static fn4j.net.uri.Fn4jNetUriArbitraries.uris;
import static fn4j.net.uri.Host.LOCALHOST;
import static fn4j.net.uri.Literal.QUESTION_MARK;
import static fn4j.net.uri.Scheme.HTTP;
import static fn4j.net.uri.Scheme.HTTPS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.jqwik.api.Combinators.combine;
import static net.jqwik.api.ShrinkingMode.FULL;

@Label("Apache HTTP Components Client")
@AddLifecycleHook(value = WireMockServerHook.class, propagateTo = PropagationMode.ALL_DESCENDANTS)
@StatisticsReport(format = NumberRangeHistogram.class)
class ApacheHcClientTest {
    @Property(tries = 200, shrinking = FULL)
    @Label("should exchange request and response")
    void shouldExchangeRequestAndResponse(WireMockServer server,
                                          @ForAll("requests") Request<byte[]> request) throws Exception {
        // given
        request = againstWireMock(server, request);
        try (var apacheHcClient = ApacheHcClient.builder().build()) {
            server.givenThat(requestMatches(request).willReturn(aResponse()));

            var tick = tick();

            // when
            var eventualResponse = apacheHcClient.exchange(request);
            consume(eventualResponse, tick);

            // then no exception is thrown
        }
    }

    //                                               Request was not matched
    //                                               =======================
    //
    //-----------------------------------------------------------------------------------------------------------------------
    //| Closest stub                                             | Request                                                  |
    //-----------------------------------------------------------------------------------------------------------------------
    //                                                           |
    //GET                                                        | GET
    ///                                                          | /
    //                                                           |
    //A:                                                         | A:
    //A:
    //A:
    //A:
    //A:
    //A: !
    //A:                         <<<<< Header does not match
    //                                                           |
    //                                                           |
    //-----------------------------------------------------------------------------------------------------------------------
    //
    //                                               Request was not matched
    //                                               =======================
    //
    //-----------------------------------------------------------------------------------------------------------------------
    //| Closest stub                                             | Request                                                  |
    //-----------------------------------------------------------------------------------------------------------------------
    //                                                           |
    //GET                                                        | GET
    ///                                                          | /
    //                                                           |
    //A:                                                         | A:
    //A: !                                            <<<<< Header does not match
    //a: !                                                       | a:
    //a: !
    //                                                           |
    //                                                           |
    //-----------------------------------------------------------------------------------------------------------------------
    //
    //
    //Process finished with exit code 130

    @Provide
    Arbitrary<Request<byte[]>> requests() {
        var methodArbitrary = Arbitraries.of(GET, HEAD, POST, PUT, DELETE, OPTIONS, PATCH);
        var uriArbitrary = uris().filter(uri -> uri.encode().length() < 8000);
        var headersArbitrary = headers().filter(headers -> headers.toString().length() < 8000);
        var maybeBodyArbitrary = bodies(byte[].class).optional().map(Option::ofOptional);

        return combine(methodArbitrary,
                       uriArbitrary,
                       headersArbitrary,
                       maybeBodyArbitrary).as(Request::request);
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

    private MappingBuilder requestMatches(Request<byte[]> request) {
        var path = request.uri().path().components();
        var queryComponents = request.uri().queryParameters().components();
        var querySeparatorComponents = queryComponents.nonEmpty() ? Stream.<UriComponent>of(QUESTION_MARK) : Stream.<UriComponent>empty();
        var pathAndQuery = concat(path, querySeparatorComponents, queryComponents).encode();
        var urlPattern = urlEqualTo(pathAndQuery);

        var mappingBuilder = WireMock.request(request.method().value(), urlPattern);

        request.headers().forEach(header -> {
            mappingBuilder.withHeader(header._1().value(), equalTo(header._2().value().trim()));
        });

//        request.headers().multimap().asMap().forEach((headerName, headerValues) -> {
//            headerValues.lastOption().forEach(headerValue -> {
//                mappingBuilder.withHeader(headerName.value(), equalTo(headerValue.value().trim()));
//            });
//        });

//        request.maybeBody().forEach(body -> {
//            mappingBuilder.withRequestBody(binaryEqualTo(body.value()));
//        });

        return mappingBuilder;
    }

    private void consume(Future<Response<byte[]>> eventualResponse,
                         TickTock.Tick tick) {
        eventualResponse.await(2, SECONDS);
        var tickTock = tick.tock();
        Statistics.label("duration in ms").collect(tickTock.duration());
    }

    public record ArbitraryUriComponentsParent(Seq<UriComponent> components) implements UriComponentParent {
        public static ArbitraryUriComponentsParent arbitraryComponents(Seq<UriComponent> components) {
            return new ArbitraryUriComponentsParent(components);
        }

        @SafeVarargs
        public static ArbitraryUriComponentsParent concat(Iterable<? extends UriComponent>... iterables) {
            return arbitraryComponents(Stream.concat(iterables));
        }
    }

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

    @Property
    void foobar(@ForAll Uri uri) {
        Assume.that(uri.maybeScheme().exists(scheme -> scheme.encode().length() > 0));
        Assertions.assertThat(uri.encode()).isEqualTo(uri.asJavaURI().toASCIIString());
    }

    @Example
    void foobar1() {
        Assertions.assertThat(new Uri("http://localhost/path?query=").encode()).isEqualTo("http://localhost/path?query=");
    }

    @Provide
    Arbitrary<?> safe(TypeUsage typeUsage) {
        if (typeUsage.isOfType(Method.class)) {
            return Arbitraries.of(GET, HEAD, POST, PUT, DELETE, OPTIONS, PATCH);
        }
        return Arbitraries.forType(typeUsage.getRawType());
    }
}