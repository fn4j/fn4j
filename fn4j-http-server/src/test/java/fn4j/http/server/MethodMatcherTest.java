package fn4j.http.server;

import fn4j.http.core.*;
import io.vavr.Tuple;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;

import static fn4j.http.core.Fn4jHttpCoreInstanceOfAssertFactories.RESPONSE;
import static fn4j.http.core.HeaderName.ALLOW;
import static fn4j.http.core.Method.POST_VALUE;
import static fn4j.http.core.Status.METHOD_NOT_ALLOWED;
import static fn4j.http.core.Status.OK;
import static fn4j.http.core.StatusCode.OK_VALUE;
import static fn4j.http.server.Handler.matchMethod;
import static fn4j.http.server.MethodMatcher.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

@Label("Method matcher")
class MethodMatcherTest {

    @Label("when matching")
    static class WhenMatching {

        @Example
        @Label("should use matching handler")
        <A, B> void shouldUseOtherHandler(@ForAll @WithMethod(POST_VALUE) Request<A> request,
                                          @ForAll Response<B> response) {
            // given
            MethodMatcher<A, B> methodMatcher = matchMethod(
                    GET(__ -> fail("expected not to match GET")),
                    POST(req -> {
                        assertThat(req).isSameAs(request);
                        return Future.successful(response);
                    }),
                    PUT(__ -> fail("expected not to match PUT"))
            );
            Handler<A, B> handler = methodMatcher.orElse(__ -> fail("expected not to use other handler"));

            // when
            Future<Response<B>> result = handler.apply(request);

            // then
            assertThat(result.toTry()).isSuccess()
                                      .extracting(Try::get)
                                      .isSameAs(response);
        }

        @Example
        @Label("should not have method not allowed")
        <A, B> void shouldNotHaveMethodNotAllowed(@ForAll @WithMethod("DELETE") Request<A> request,
                                                  @ForAll @WithStatus(OK_VALUE) Response<B> response) {
            // given
            MethodMatcher<A, B> methodMatcher = matchMethod(
                    DELETE(req -> {
                        assertThat(req).isSameAs(request);
                        return Future.successful(response);
                    }),
                    GET(__ -> fail("expected not to match GET")),
                    PUT(__ -> fail("expected not to match PUT"))
            );
            Handler<A, B> handler = methodMatcher.orMethodNotAllowed();

            // when
            Future<Response<B>> result = handler.apply(request);

            // then
            assertThat(result.toTry()).isSuccess()
                                      .extracting(Try::get)
                                      .isSameAs(response)
                                      .asInstanceOf(RESPONSE)
                                      .hasStatus(OK);
        }
    }

    @Label("when not matching")
    static class WhenNotMatching {

        @Example
        @Label("should use other handler")
        <A, B> void shouldUseOtherHandler(@ForAll @WithMethod("PUT") Request<A> request,
                                          @ForAll Response<B> response) {
            // given
            MethodMatcher<A, B> methodMatcher = matchMethod(
                    GET(__ -> fail("expected not to match GET")),
                    POST(__ -> fail("expected not to match POST"))
            );
            Handler<A, B> handler = methodMatcher.orElse(req -> {
                assertThat(req).isSameAs(request);
                return Future.successful(response);
            });

            // when
            Future<Response<B>> result = handler.apply(request);

            // then
            assertThat(result.toTry()).isSuccess()
                                      .extracting(Try::get)
                                      .isSameAs(response);
        }

        @Example
        @Label("should have method not allowed")
        <A, B> void shouldHaveMethodNotAllowed(@ForAll @WithMethod("PATCH") Request<A> request) {
            // given
            MethodMatcher<A, B> methodMatcher = matchMethod(
                    GET(__ -> fail("expected not to match GET")),
                    POST(__ -> fail("expected not to match POST"))
            );
            Handler<A, B> handler = methodMatcher.orMethodNotAllowed();

            // when
            Future<Response<B>> result = handler.apply(request);

            // then
            assertThat(result.toTry()).isSuccess()
                                      .extracting(Try::get)
                                      .asInstanceOf(RESPONSE)
                                      .hasStatus(METHOD_NOT_ALLOWED)
                                      .hasNoBody()
                                      .extracting(Head::headers)
                                      .satisfies(headers -> {
                                          // TODO: fn4j-http-core-assertj
                                          assertThat(headers.stream()).contains(Tuple.of(ALLOW, new HeaderValue("GET,POST")));
                                      });
        }
    }

    @Label("when empty")
    static class WhenEmpty {

        @Example
        @Label("should use other handler")
        <A, B> void shouldUseOtherHandler(@ForAll Request<A> request,
                                          @ForAll Response<B> response) {
            // given
            MethodMatcher<A, B> methodMatcher = matchMethod();
            Handler<A, B> handler = methodMatcher.orElse(req -> {
                assertThat(req).isSameAs(request);
                return Future.successful(response);
            });

            // when
            Future<Response<B>> result = handler.apply(request);

            // then
            assertThat(result.toTry()).isSuccess()
                                      .extracting(Try::get)
                                      .isSameAs(response);
        }

        @Example
        @Label("should have method not allowed")
        <A, B> void shouldHaveMethodNotAllowed(@ForAll Request<A> request) {
            // given
            MethodMatcher<A, B> methodMatcher = matchMethod();
            Handler<A, B> handler = methodMatcher.orMethodNotAllowed();

            // when
            Future<Response<B>> result = handler.apply(request);

            // then
            assertThat(result.toTry()).isSuccess()
                                      .extracting(Try::get)
                                      .asInstanceOf(RESPONSE)
                                      .hasStatus(METHOD_NOT_ALLOWED)
                                      .hasNoBody()
                                      .extracting(Response::headers)
                                      .satisfies(headers -> {
                                          // TODO: fn4j-http-core-assertj
                                          assertThat(headers.stream()).contains(Tuple.of(ALLOW, new HeaderValue("")));
                                      });
        }
    }
}