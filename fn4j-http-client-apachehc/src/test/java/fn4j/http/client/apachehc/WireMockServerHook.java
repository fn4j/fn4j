package fn4j.http.client.apachehc;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.VerificationException;
import net.jqwik.api.lifecycle.*;
import org.opentest4j.AssertionFailedError;

import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static net.jqwik.api.lifecycle.Lifespan.RUN;

class WireMockServerHook implements BeforeContainerHook, ResolveParameterHook, AroundTryHook {
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