package fn4j.http.routing;

import fn4j.http.core.Request;
import fn4j.http.core.Response;
import fn4j.http.core.WithStatus;
import fn4j.http.core.WithUri;
import fn4j.net.uri.Path;
import io.vavr.Tuple;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;

import java.util.concurrent.atomic.AtomicBoolean;

import static fn4j.http.core.Fn4jHttpCoreInstanceOfAssertFactories.RESPONSE;
import static fn4j.http.core.Status.NOT_FOUND;
import static fn4j.http.core.Status.OK;
import static fn4j.http.core.StatusCode.OK_VALUE;
import static fn4j.http.routing.Handler.matchPath;
import static fn4j.http.routing.Handler.pathCase;
import static fn4j.http.routing.PathPattern.Root;
import static fn4j.net.uri.Path.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

@Label("Path matcher")
class PathMatcherTest {

    @Label("when matching")
    static class WhenMatching {

        @Example
        @Label("should use matching handler")
        <A, B, P> void shouldUseMatchingHandler(@ForAll @WithUri("http://host/path/with/id/10?query_parameter=test") Request<A> request,
                                                @ForAll Response<B> response,
                                                @ForAll P pathParameters) {
            // given
            PathMatcher<A, B> pathMatcher = matchPath(
                    pathCase(path -> {
                                 assertThat(path).isEqualTo(new Path("/path/with/id/10"));
                                 return Option.of(pathParameters);
                             },
                             pathPar -> req -> {
                                 assertThat(pathPar).isSameAs(pathParameters);
                                 assertThat(req).isSameAs(request);
                                 return Future.successful(response);
                             })
            );
            Handler<A, B> handler = pathMatcher.orElse(__ -> fail("expected not to use other handler"));

            // when
            Future<Response<B>> result = handler.apply(request);

            // then
            assertThat(result.toTry()).isSuccess()
                                      .extracting(Try::get)
                                      .isSameAs(response);
        }

        @Example
        @Label("should use first matching handler")
        <A, B, P> void shouldUseFirstMatchingHandler(@ForAll @WithUri("http://host/") Request<A> request,
                                                     @ForAll Response<B> response,
                                                     @ForAll P pathParameters) {
            // given
            AtomicBoolean markerA = new AtomicBoolean(false);
            AtomicBoolean markerB = new AtomicBoolean(false);
            PathMatcher<A, B> pathMatcher = matchPath(
                    pathCase(path -> {
                                 assertThat(path).isEqualTo(EMPTY);
                                 markerA.set(true);
                                 return Option.none();
                             },
                             __ -> fail("expected not to use handler")),
                    pathCase(path -> {
                                 assertThat(path).isEqualTo(EMPTY);
                                 markerB.set(true);
                                 return Option.none();
                             },
                             __ -> fail("expected not to use handler")),
                    pathCase(path -> {
                                 assertThat(path).isEqualTo(EMPTY);
                                 return Option.of(pathParameters);
                             },
                             pathPar -> req -> {
                                 assertThat(pathPar).isSameAs(pathParameters);
                                 assertThat(req).isSameAs(request);
                                 return Future.successful(response);
                             }),
                    pathCase(path -> fail("expected not to use handler"),
                             __ -> fail("expected not to use handler"))
            );
            Handler<A, B> handler = pathMatcher.orElse(__ -> fail("expected not to use other handler"));

            // when
            Future<Response<B>> result = handler.apply(request);

            // then
            assertThat(result.toTry()).isSuccess()
                                      .extracting(Try::get)
                                      .isSameAs(response);
            assertThat(markerA).isTrue();
            assertThat(markerB).isTrue();
        }

        @Example
        @Label("should not have Not Found")
        <A, B, P> void shouldNotHaveNotFound(@ForAll @WithUri("http://host/path") Request<A> request,
                                             @ForAll @WithStatus(OK_VALUE) Response<B> response,
                                             @ForAll P pathParameters) {
            // given
            PathMatcher<A, B> pathMatcher = matchPath(
                    pathCase(path -> {
                                 assertThat(path).isEqualTo(new Path("/path"));
                                 return Option.of(pathParameters);
                             },
                             pathPar -> req -> {
                                 assertThat(pathPar).isSameAs(pathParameters);
                                 assertThat(req).isSameAs(request);
                                 return Future.successful(response);
                             })
            );
            Handler<A, B> handler = pathMatcher.orNotFound();

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
        <A, B> void shouldUseOtherHandler(@ForAll @WithUri("http://host/") Request<A> request,
                                          @ForAll Response<B> response) {
            // given
            PathMatcher<A, B> pathMatcher = matchPath(
                    pathCase(path -> {
                                 assertThat(path).isEqualTo(EMPTY);
                                 return Option.none();
                             },
                             __ -> fail("expected not to use handler"))
            );
            Handler<A, B> handler = pathMatcher.orElse(req -> {
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
        @Label("should have Not Found")
        <A, B> void shouldHaveNotFound(@ForAll @WithUri("http://host/") Request<A> request) {
            // given
            PathMatcher<A, B> pathMatcher = matchPath(
                    pathCase(path -> {
                                 assertThat(path).isEqualTo(EMPTY);
                                 return Option.none();
                             },
                             __ -> fail("expected not to use handler"))
            );
            Handler<A, B> handler = pathMatcher.orNotFound();

            // when
            Future<Response<B>> result = handler.apply(request);

            // then
            assertThat(result.toTry()).isSuccess()
                                      .extracting(Try::get)
                                      .asInstanceOf(RESPONSE)
                                      .hasStatus(NOT_FOUND)
                                      .hasNoBody()
                                      .extracting(Response::headers)
                                      .satisfies(headers -> {
                                          // TODO: fn4j-http-core-assertj
                                          assertThat(headers.multimap()).isEmpty();
                                      });
        }
    }

    @Label("when used with path patterns")
    static class WhenUsedWithPathPatterns {

        @Example
        @Label("should match")
        <A, B> void shouldMatch(@ForAll @WithUri("http://host/resource/1234") Request<A> request,
                                @ForAll Response<B> response) {
            // given
            Handler<A, B> handler = matchPath(
                    pathCase(Root.slash("resource"),
                             __ -> fail("expected not to use handler")),
                    pathCase(Root.slash("resource").slash("1234"),
                             pathPar -> (Request<A> req) -> {
                                 assertThat(pathPar).isEqualTo(Tuple.empty());
                                 assertThat(req).isSameAs(request);
                                 return Future.successful(response);
                             })
            ).orNotFound();

            // when
            Future<Response<B>> result = handler.apply(request);

            // then
            assertThat(result.toTry()).isSuccess()
                                      .extracting(Try::get)
                                      .isSameAs(response);
        }
    }
}