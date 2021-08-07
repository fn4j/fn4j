package fn4j.http.server;

import fn4j.http.core.Request;
import fn4j.http.core.Response;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

class MethodMatcherTest {
    @Example
    <A, B> void shouldUseOtherHandler(@ForAll Request<A> request, @ForAll Response<B> response) {
        // given
        Handler<A, B> otherHandler = req -> {
            assertThat(req).isSameAs(request);
            return Future.successful(response);
        };
        MethodMatcher<A, B> methodMatcher = Handler.matchMethod();
        Handler<A, B> handler = methodMatcher.orElse(otherHandler);

        // when
        Future<Response<B>> result = handler.apply(request);

        // then
        assertThat(result.toTry()).isSuccess()
                                  .extracting(Try::get)
                                  .isSameAs(response);
    }
}