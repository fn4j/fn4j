package fn4j.http.server.apachehc;

import fn4j.http.core.Headers;
import io.vavr.concurrent.Future;
import io.vavr.concurrent.Promise;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static fn4j.http.core.Response.response;
import static fn4j.http.core.Status.OK;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ApacheHcServerTest {

    @Test
    void shouldTest() throws InterruptedException, ExecutionException, TimeoutException {
        // given
        var port = new Random().nextInt(10000) + 30000;
        var inetSocketAddress = new InetSocketAddress(port);
        var server = ApacheHcServer.builder()
                                   .inetSocketAddress(inetSocketAddress)
                                   .noShutdownAutoCloseMode()
                                   .handler(request -> Future.successful(response(OK, Headers.empty(), request.maybeBody()))).build();

        try (var closer = server.open().await(2, SECONDS).get()) {
            var httpClient = HttpClient.newHttpClient();
            var httpRequest = HttpRequest.newBuilder(URI.create("http://localhost:%d/".formatted(port)))
                                         .POST(BodyPublishers.ofString("<body>"))
                                         .build();

            // when
            var result = httpClient.sendAsync(httpRequest, BodyHandlers.ofString()).get(2, SECONDS);

            // then
            assertThat(result.body()).isEqualTo("<body>");

            // given
            AtomicBoolean closedMarker = new AtomicBoolean(false);
            Promise<Void> promise = Promise.make();
            new Thread(() -> closer.awaitClose().onComplete(promise::complete).forEach(__ -> closedMarker.set(true))).start();

            // then
            assertThat(closedMarker).isFalse();

            // when
            closer.close();

            // then
            promise.future()
                   .await(2, SECONDS)
                   .onSuccess(__ -> assertThat(closedMarker).isTrue())
                   .onFailure(error -> fail("expected no error while waiting for close", error));
        }
    }
}