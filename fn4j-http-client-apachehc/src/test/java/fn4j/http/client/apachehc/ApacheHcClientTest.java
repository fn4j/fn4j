package fn4j.http.client.apachehc;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import fn4j.http.core.Body;
import fn4j.http.core.Method;
import fn4j.http.core.Request;
import fn4j.http.core.Response;
import fn4j.http.core.header.BearerAuthenticationHeader;
import fn4j.http.core.header.HeaderName;
import fn4j.http.core.header.HeaderValue;
import fn4j.http.core.header.RawHeader;
import fn4j.net.uri.Uri;
import io.vavr.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static fn4j.http.core.Request.request;
import static fn4j.http.core.header.BearerAuthenticationHeader.Token.token;
import static fn4j.http.core.header.Headers.headers;
import static io.vavr.API.TODO;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

class ApacheHcClientTest {
    @RegisterExtension
    WireMockExtension server = WireMockExtension.newInstance()
                                                .options(wireMockConfig().dynamicPort())
                                                .failOnUnmatchedRequests(true)
                                                .build();

    @Test
    void shouldExchangeRequestAndResponse() throws Exception {
        // given
        try (var apacheHcClient = ApacheHcClient.builder().build()) {
            Request<byte[]> request = request(Method.POST,
                                              new Uri(server.url("/path?parameter=value&flag")),
                                              headers(new RawHeader(new HeaderName("name"),
                                                                    new HeaderValue("value")),
                                                      new BearerAuthenticationHeader(token("token"))),
                                              new Body<>("Body".getBytes(UTF_8)));

            server.givenThat(
                    post(urlEqualTo("/path?parameter=value&flag"))
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
    }
}