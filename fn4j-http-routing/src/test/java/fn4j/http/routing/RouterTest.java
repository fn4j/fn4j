package fn4j.http.routing;

import fn4j.http.core.HeaderValue;
import fn4j.http.core.Method;
import fn4j.http.core.Request;
import fn4j.http.core.Response;
import fn4j.net.uri.Path;
import io.vavr.Tuple;
import io.vavr.Tuple0;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.collection.TreeSet;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import org.assertj.core.api.Assertions;

import java.util.function.Function;

import static fn4j.http.core.Fn4jHttpCoreAssertions.assertThat;
import static fn4j.http.core.HeaderName.ALLOW;
import static fn4j.http.core.Method.COMMON_METHODS;
import static fn4j.http.core.Status.METHOD_NOT_ALLOWED;
import static fn4j.http.core.Status.NOT_FOUND;
import static fn4j.http.routing.Route.route;
import static fn4j.http.routing.Router.router;
import static fn4j.net.uri.Path.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

class RouterTest {
    @Property(tries = 100)
    @Label("should route request")
    <A, P, B> void shouldRouteRequest(@ForAll Request<A> request,
                                      @ForAll P parameter,
                                      @ForAll Response<B> response) {
        // given
        Router<A, B> router = router(route(request.method(),
                                           (PathPattern<P>) path -> {
                                               assertThat(path).isEqualTo(request.uri().path());
                                               return matchingWith(parameter);
                                           },
                                           (Function<P, Handler<A, B>>) actualParameter -> actualRequest -> {
                                               assertThat(actualParameter).isSameAs(parameter);
                                               assertThat(actualRequest).isSameAs(request);
                                               return Future.successful(response);
                                           }));

        // when
        Future<Response<B>> result = router.apply(request);

        // then
        assertThat(result.toTry()).containsSame(response);
    }

    @Property(tries = 100)
    @Label("should route request to first matching route by path")
    <A, P, B> void shouldRouteRequestToFirstMatchingRouteByPath(@ForAll Request<A> request,
                                                                @ForAll P parameter,
                                                                @ForAll Response<B> response) {
        // given
        Router<A, B> router = router(route(request.method(),
                                           (PathPattern<Tuple0>) path -> {
                                               assertThat(path).isEqualTo(request.uri().path());
                                               return notMatching();
                                           },
                                           (Handler<A, B>) actualRequest -> fail("expected not to use first route")),
                                     route(request.method(),
                                           (PathPattern<P>) path -> {
                                               assertThat(path).isEqualTo(request.uri().path());
                                               return matchingWith(parameter);
                                           },
                                           (Function<P, Handler<A, B>>) actualParameter -> actualRequest -> {
                                               assertThat(actualParameter).isSameAs(parameter);
                                               assertThat(actualRequest).isSameAs(request);
                                               return Future.successful(response);
                                           }),
                                     route(request.method(),
                                           (PathPattern<Tuple0>) path -> fail("expected not to evaluate third route"),
                                           (Handler<A, B>) actualRequest -> fail("expected not to use third route")));

        // when
        Future<Response<B>> result = router.apply(request);

        // then
        assertThat(result.toTry()).containsSame(response);
    }

    @Property(tries = 100)
    @Label("should route request to first matching route by method")
    <A, P, B> void shouldRouteRequestToFirstMatchingRouteByMethod(@ForAll Request<A> request,
                                                                  @ForAll P parameter,
                                                                  @ForAll Response<B> response) {
        // given
        var matchingMethod = request.method();
        var differentMethod = otherMethodThan(matchingMethod);
        Router<A, B> router = router(route(differentMethod,
                                           (PathPattern<Tuple0>) path -> {
                                               assertThat(path).isEqualTo(request.uri().path());
                                               return matchingWith(Tuple.empty());
                                           },
                                           (Handler<A, B>) actualRequest -> fail("expected not to use first route")),
                                     route(matchingMethod,
                                           (PathPattern<P>) path -> {
                                               assertThat(path).isEqualTo(request.uri().path());
                                               return matchingWith(parameter);
                                           },
                                           (Function<P, Handler<A, B>>) actualParameter -> actualRequest -> {
                                               Assertions.assertThat(actualParameter).isSameAs(parameter);
                                               assertThat(actualRequest).isSameAs(request);
                                               return Future.successful(response);
                                           }),
                                     route(matchingMethod,
                                           (PathPattern<Tuple0>) path -> fail("expected not to evaluate third route"),
                                           (Handler<A, B>) actualRequest -> fail("expected not to use third route")));

        // when
        Future<Response<B>> result = router.apply(request);

        // then
        assertThat(result.toTry()).containsSame(response);
    }

    @Property(tries = 100)
    @Label("should have Not Found without any routes")
    <A, B> void shouldHaveNotFoundWithoutAnyRoutes(@ForAll Request<A> request) {
        // given
        Router<A, B> router = new Router<>(Stream.empty());

        // when
        Future<Response<B>> result = router.apply(request);

        // then
        assertThat(result.toTry()).hasValueSatisfying(response -> assertThat(response).hasStatus(NOT_FOUND)
                                                                                      .hasNoBody());
    }

    @Property(tries = 100)
    @Label("should have Not Found if no route matches")
    <A, B> void shouldHaveNotFoundIfNoRouteMatches(@ForAll Request<A> request) {
        // given
        Router<A, B> router = router(route(request.method(),
                                           (PathPattern<Tuple0>) path -> {
                                               assertThat(path).isEqualTo(request.uri().path());
                                               return notMatching();
                                           },
                                           (Handler<A, B>) actualRequest -> fail("expected not to use first route")),
                                     route(request.method(),
                                           (PathPattern<Tuple0>) path -> {
                                               assertThat(path).isEqualTo(request.uri().path());
                                               return notMatching();
                                           },
                                           (Handler<A, B>) actualRequest -> fail("expected not to use second route")),
                                     route(request.method(),
                                           (PathPattern<Tuple0>) path -> {
                                               assertThat(path).isEqualTo(request.uri().path());
                                               return notMatching();
                                           },
                                           (Handler<A, B>) actualRequest -> fail("expected not to use third route")));

        // when
        Future<Response<B>> result = router.apply(request);

        // then
        assertThat(result.toTry()).hasValueSatisfying(actualResponse -> assertThat(actualResponse).hasStatus(NOT_FOUND)
                                                                                                  .hasNoBody());
    }

    @Property(tries = 100)
    @Label("should have Method Not Allowed if no route matches")
    <A, B> void shouldHaveMethodNotAllowedIfNoRouteMatches(@ForAll Request<A> request) {
        // given
        var firstDifferentMethod = otherMethodThan(request.method());
        var secondDifferentMethod = otherMethodThan(request.method(), firstDifferentMethod);
        var thirdDifferentMethod = otherMethodThan(request.method(), firstDifferentMethod, secondDifferentMethod);
        Router<A, B> router = router(route(firstDifferentMethod,
                                           (PathPattern<Tuple0>) path -> {
                                               assertThat(path).isEqualTo(request.uri().path());
                                               return matchingWith(Tuple.empty());
                                           },
                                           (Handler<A, B>) actualRequest -> fail("expected not to use first route")),
                                     route(secondDifferentMethod,
                                           (PathPattern<Tuple0>) path -> {
                                               assertThat(path).isEqualTo(request.uri().path());
                                               return matchingWith(Tuple.empty());
                                           },
                                           (Handler<A, B>) actualRequest -> fail("expected not to use second route")),
                                     route(thirdDifferentMethod,
                                           (PathPattern<Tuple0>) path -> {
                                               assertThat(path).isEqualTo(request.uri().path());
                                               return matchingWith(Tuple.empty());
                                           },
                                           (Handler<A, B>) actualRequest -> fail("expected not to use third route")));

        // when
        Future<Response<B>> result = router.apply(request);

        // then
        var allowedMethods = TreeSet.of(firstDifferentMethod, secondDifferentMethod, thirdDifferentMethod);
        var acceptHeaderValue = new HeaderValue(allowedMethods.toStream().map(Method::value).mkString(","));
        assertThat(result.toTry()).hasValueSatisfying(actualResponse -> {
            assertThat(actualResponse).hasStatus(METHOD_NOT_ALLOWED).hasNoBody();
            assertThat(actualResponse.headers()).containsEntry(ALLOW, acceptHeaderValue);
        });
    }

    private Method otherMethodThan(Method... methods) {
        return otherMethodThan(Stream.of(methods));
    }

    private Method otherMethodThan(Seq<Method> methods) {
        return COMMON_METHODS.removeAll(methods).head();
    }

    private <P> Option<Tuple2<P, Path>> matchingWith(P parameter) {
        return Option.of(Tuple.of(parameter, EMPTY));
    }

    private Option<Tuple2<Tuple0, Path>> notMatching() {
        return Option.none();
    }
}