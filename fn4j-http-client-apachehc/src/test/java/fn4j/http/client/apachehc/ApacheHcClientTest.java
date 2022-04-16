package fn4j.http.client.apachehc;

import fn4j.http.core.Request;
import org.junit.jupiter.api.Test;

import static io.vavr.API.TODO;
import static org.assertj.core.api.Assertions.assertThat;

class ApacheHcClientTest {
    @Test
    void shouldExchangeRequestAndResponse() throws Exception {
        // given
        try (var apacheHcClient = ApacheHcClient.builder().build()) {
            Request<byte[]> request = TODO(); // TODO

            // when
            var eventualResponse = apacheHcClient.exchange(request);

            // then
            assertThat(eventualResponse).isNotNull();
        }
    }
}