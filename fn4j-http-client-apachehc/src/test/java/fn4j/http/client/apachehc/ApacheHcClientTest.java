package fn4j.http.client.apachehc;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import fn4j.http.core.Body;
import fn4j.http.core.Method;
import fn4j.http.core.Response;
import fn4j.http.core.header.Headers;
import fn4j.net.uri.Path;
import fn4j.net.uri.Query;
import fn4j.net.uri.Uri;
import fn4j.net.uri.UriComponent;
import io.vavr.collection.Stream;
import io.vavr.concurrent.Future;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static fn4j.http.core.Request.request;
import static fn4j.net.uri.Literal.QUESTION_MARK;
import static io.vavr.API.TODO;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

@Label("Apache HTTP Components Client")
class ApacheHcClientTest {
    @Property
    @Label("should exchange request and response")
    void shouldExchangeRequestAndResponse(@ForAll Method method,
                                          @ForAll Path path,
                                          @ForAll Query query,
                                          @ForAll Headers headers,
                                          @ForAll byte[] body) throws Exception {
        // given
        WireMockExtension server = WireMockExtension.newInstance()
                                                    .options(wireMockConfig().dynamicPort())
                                                    .failOnUnmatchedRequests(true)
                                                    .build();
        try {
            var pathAndQueryString = Stream.<UriComponent>empty()
                                           .appendAll(path.pathSegments())
                                           .append(QUESTION_MARK)
                                           .append(query)
                                           .map(UriComponent::encode)
                                           .mkString();
            var uri = new Uri(server.url(pathAndQueryString));
            var request = request(method,
                                  uri,
                                  headers,
                                  new Body<>(body));

            try (var apacheHcClient = ApacheHcClient.builder().build()) {
                server.givenThat(
                        post(urlEqualTo(pathAndQueryString))
                                .withHeader("name", equalTo("value"))
                                .withHeader("Authentication", equalTo("Bearer token"))
                                .withRequestBody(equalTo("Body"))
                                .willReturn(
                                        aResponse().withBody(new byte[]{(byte) 0x80})
                                )
                );

                // when
                Future<Response<byte[]>> eventualResponse = apacheHcClient.exchange(request);

                // then
                assertThat(eventualResponse.await(2, SECONDS).toTry()).isSuccess().hasValueSatisfying(response -> {
                    TODO();
                });
            }
        } finally {
            server.shutdownServer();
        }
    }
}