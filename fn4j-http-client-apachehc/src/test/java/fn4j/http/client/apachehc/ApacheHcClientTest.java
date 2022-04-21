package fn4j.http.client.apachehc;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import fn4j.http.core.Method;
import fn4j.http.core.Request;
import fn4j.http.core.Response;
import fn4j.http.core.header.Headers;
import fn4j.net.uri.Uri;
import io.vavr.concurrent.Future;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.providers.TypeUsage;
import org.opentest4j.AssertionFailedError;

import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static fn4j.http.core.Method.*;
import static fn4j.http.core.Request.request;
import static fn4j.http.core.Status.OK;
import static fn4j.http.core.StatusCode.OK_VALUE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.jqwik.api.lifecycle.Lifespan.RUN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

@Label("Apache HTTP Components Client")
@AddLifecycleHook(value = ApacheHcClientTest.WireMockServerHook.class, propagateTo = PropagationMode.ALL_DESCENDANTS)
class ApacheHcClientTest {
    @Property
    @Label("should exchange request and response")
    void shouldExchangeRequestAndResponse(WireMockServer server,
                                          @ForAll("safe") Method method) throws Exception {
        // given
        Request<byte[]> request = request(method,
                                          new Uri(server.baseUrl()),
                                          Headers.empty());

        try (var apacheHcClient = ApacheHcClient.builder().build()) {
            server.givenThat(WireMock.request(method.value(), UrlPattern.ANY).willReturn(aResponse().withStatus(OK_VALUE)));

            // when
            Future<Response<byte[]>> eventualResponse = apacheHcClient.exchange(request);

            // then
            assertThat(eventualResponse.await(2, SECONDS).toTry()).isSuccess().hasValueSatisfying(response -> {
                assertThat(response.status()).isEqualTo(OK);
            });
        }
    }

    @Provide
    Arbitrary<?> safe(TypeUsage typeUsage) {
        if (typeUsage.isOfType(Method.class)) {
            return Arbitraries.of(GET, HEAD, POST, PUT, DELETE, OPTIONS, PATCH);
        }
        return Arbitraries.forType(typeUsage.getRawType());
    }

    static class WireMockServerHook implements BeforeContainerHook, ResolveParameterHook, AroundTryHook {
        @Override
        public void beforeContainer(ContainerLifecycleContext context) {
            Store.create(ClosingWireMockServer.class, RUN, () -> {
                var server = new WireMockServer(wireMockConfig().dynamicPort());
                server.start();
                return new ClosingWireMockServer(server);
            });
        }

        @Override
        public Optional<ParameterSupplier> resolve(ParameterResolutionContext parameterContext,
                                                   LifecycleContext lifecycleContext) {
            if (parameterContext.typeUsage().isOfType(WireMockServer.class)) {
                return Optional.of(__ -> getWireMockServerFromStore());
            }
            return Optional.empty();
        }

        @Override
        public TryExecutionResult aroundTry(TryLifecycleContext context,
                                            TryExecutor aTry,
                                            List<Object> parameters) {
            var originalTryExecutionResult = aTry.execute(parameters);
            var wireMockServer = getWireMockServerFromStore();
            try {
                wireMockServer.checkForUnmatchedRequests();
            } catch (VerificationException verificationException) {
                return TryExecutionResult.falsified(new AssertionFailedError(verificationException.getMessage(), verificationException));
            } finally {
                wireMockServer.resetAll();
            }
            return originalTryExecutionResult;
        }

        private WireMockServer getWireMockServerFromStore() {
            return Store.<ClosingWireMockServer>get(ClosingWireMockServer.class).get().wireMockServer();
        }

        private record ClosingWireMockServer(WireMockServer wireMockServer) implements Store.CloseOnReset {
            @Override
            public void close() {
                wireMockServer.stop();
            }
        }
    }
}