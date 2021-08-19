package fn4j.http.routing.example;

import fn4j.http.core.Body;
import fn4j.http.core.HeaderValue;
import fn4j.http.core.Headers;
import fn4j.http.routing.Handler;
import fn4j.http.routing.PathPattern.PathSegmentPattern;
import fn4j.http.routing.Route;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;

import java.util.function.Function;

import static fn4j.http.core.HeaderName.AUTHENTICATION;
import static fn4j.http.core.Method.GET;
import static fn4j.http.core.Response.response;
import static fn4j.http.core.Status.OK;
import static fn4j.http.core.Status.UNAUTHORIZED;
import static fn4j.http.routing.PathPattern.pathPattern;
import static fn4j.http.routing.PathPatterns.uuidTry;
import static fn4j.http.routing.Route.route;
import static io.vavr.API.TODO;

public record WishlistController(AuthenticationService authenticationService,
                                 WishlistService wishlistService) {
    private static final PathSegmentPattern<Try<WishlistId>> WISHLIST_ID_PATH_PATTERN =
            uuidTry().mapSegment(maybeUuid -> maybeUuid.map(WishlistId::new));

    public Seq<Route<?, String, String>> routes() {
        return Stream.of(route(GET, "wishlist", getWishlistForAuthenticatedUser()),
                         route(GET, "wishlists", getWishlists()),
                         route(GET, pathPattern("wishlists").slash(WISHLIST_ID_PATH_PATTERN), getWishlist()));
    }

    private Handler<String, String> getWishlistForAuthenticatedUser() {
        return request -> {
            var maybeAuthenticationHeaderValue = request.headers().get(AUTHENTICATION).singleOption();
            var maybeAuthenticationToken = maybeAuthenticationHeaderValue.map(HeaderValue::value);
            var maybeIdOfAuthenticatedUser = maybeAuthenticationToken.flatMap(authenticationToken -> {
                return authenticationService.maybeIdOfAuthenticatedUser(authenticationToken, request);
            });
            return maybeIdOfAuthenticatedUser.fold(() -> {
                return Future.successful(response(UNAUTHORIZED, Headers.empty()));
            }, userId -> {
                var wishlist = wishlistService.wishlistOfUser(userId);
                return Future.successful(response(OK, Headers.empty(), new Body<>(wishlist)));
            });
        };
    }

    private Handler<String, String> getWishlists() {
        return request -> {
            var wishlists = wishlistService.allWishlists(request);
            return Future.successful(response(OK, Headers.empty(), new Body<>(wishlists)));
        };
    }

    private Function<Try<WishlistId>, Handler<String, String>> getWishlist() {
        return maybeWishlistId -> request -> {
            return maybeWishlistId.fold(error -> TODO("invalid wishlist id -> bad request"), wishlistId -> {
                var maybeWishlist = wishlistService.findWishlist(wishlistId);
                return maybeWishlist.fold(() -> TODO("no wishlist -> not found"), wishlist -> {
                    return Future.successful(response(OK, Headers.empty(), new Body<>(wishlist)));
                });
            });
        };
    }
}