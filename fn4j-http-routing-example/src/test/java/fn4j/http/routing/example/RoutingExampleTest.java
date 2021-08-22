package fn4j.http.routing.example;

import fn4j.http.core.Body;
import fn4j.http.core.Request;
import fn4j.http.core.Response;
import fn4j.http.core.header.BearerAuthenticationHeader;
import fn4j.http.core.header.BearerAuthenticationHeader.Token;
import fn4j.http.core.header.Headers;
import fn4j.http.routing.Router;
import fn4j.net.uri.Uri;
import io.vavr.Tuple;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static fn4j.http.core.Fn4jHttpCoreAssertions.assertThat;
import static fn4j.http.core.Method.*;
import static fn4j.http.core.Request.request;
import static fn4j.http.core.Status.*;
import static fn4j.http.core.header.Headers.headers;
import static fn4j.http.core.header.LocationHeader.location;
import static fn4j.http.routing.Router.router;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class RoutingExampleTest {
    private final UserService userService = mock(UserService.class);
    private final AuthenticationService authenticationService = mock(AuthenticationService.class);
    private final WishlistService wishlistService = mock(WishlistService.class);

    private final UsersController usersController = new UsersController(userService);
    private final AdminController adminController = new AdminController(usersController);
    private final WishlistController wishlistController = new WishlistController(authenticationService, wishlistService);
    private final RootController rootController = new RootController(adminController, wishlistController);

    private final Router<String, String> router = router(rootController.routes());

    @Test
    void shouldHaveAllUsers() {
        // given
        Request<String> request = request(GET,
                                          new Uri("http://www.example.com/admin/users"),
                                          Headers.empty());

        given(userService.allUsers(request)).willReturn("<users>");

        // when
        Future<Response<String>> result = router.apply(request);

        // then
        assertThat(result.toTry()).hasValueSatisfying(response -> assertThat(response).hasStatus(OK)
                                                                                      .hasBody("<users>"));
    }

    @Test
    void shouldCreateUser() {
        // given
        Request<String> request = request(POST,
                                          new Uri("http://www.example.com/admin/users"),
                                          Headers.empty(),
                                          new Body<>("<user-data>"));

        given(userService.createUser(request)).willReturn("<user>");

        // when
        Future<Response<String>> result = router.apply(request);

        // then
        assertThat(result.toTry()).hasValueSatisfying(response -> {
            assertThat(response).hasStatus(CREATED)
                                .hasNoBody();

            var maybeLocationHeader = response.headers().getSingle(location());
            assertThat(maybeLocationHeader).hasValueSatisfying(locationHeader -> {
                assertThat(locationHeader.uri()).isEqualTo(new Uri("http://www.example.com/admin/users/%3Cuser%3E"));
            });
        });
    }

    @Test
    void shouldHaveUser() {
        // given
        Request<String> request = request(GET,
                                          new Uri("http://www.example.com/admin/users/00000000-0000-0000-0000-000000000001"),
                                          Headers.empty());

        given(userService.findUser(new UserId(UUID.fromString("00000000-0000-0000-0000-000000000001")), request)).willReturn(Option.of("<user>"));

        // when
        Future<Response<String>> result = router.apply(request);

        // then
        assertThat(result.toTry()).hasValueSatisfying(response -> assertThat(response).hasStatus(OK)
                                                                                      .hasBody("<user>"));
    }

    @Test
    void shouldHaveUserNotFound() {
        // given
        Request<String> request = request(GET,
                                          new Uri("http://www.example.com/admin/users/00000000-0000-0000-0000-000000000001"),
                                          Headers.empty());

        given(userService.findUser(any(), same(request))).willReturn(Option.none());

        // when
        Future<Response<String>> result = router.apply(request);

        // then
        assertThat(result.toTry()).hasValueSatisfying(response -> assertThat(response).hasStatus(NOT_FOUND)
                                                                                      .hasNoBody());
    }

    @Test
    void shouldHaveBadRequestIfInvalidUserId() {
        // given
        Request<String> request = request(GET,
                                          new Uri("http://www.example.com/admin/users/invalid-id"),
                                          Headers.empty());

        // when
        Future<Response<String>> result = router.apply(request);

        // then
        assertThat(result.toTry()).hasValueSatisfying(response -> assertThat(response).hasStatus(BAD_REQUEST)
                                                                                      .hasNoBody());

        then(userService).shouldHaveNoInteractions();
    }

    @Test
    void shouldUpdateUser() {
        // given
        Request<String> request = request(PUT,
                                          new Uri("http://www.example.com/admin/users/00000000-0000-0000-0000-000000000001"),
                                          Headers.empty(),
                                          new Body<>("<user-update-data>"));

        given(userService.updateUser(new UserId(UUID.fromString("00000000-0000-0000-0000-000000000001")), request)).willReturn(Option.some(Tuple.empty()));

        // when
        Future<Response<String>> result = router.apply(request);

        // then
        assertThat(result.toTry()).hasValueSatisfying(response -> assertThat(response).hasStatus(NO_CONTENT)
                                                                                      .hasNoBody());
    }

    @Test
    void shouldDeleteUser() {
        // given
        Request<String> request = request(DELETE,
                                          new Uri("http://www.example.com/admin/users/00000000-0000-0000-0000-000000000001"),
                                          Headers.empty());

        UserId userId = new UserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        given(userService.deleteUser(userId, request)).willReturn(Option.some(Tuple.empty()));

        // when
        Future<Response<String>> result = router.apply(request);

        // then
        assertThat(result.toTry()).hasValueSatisfying(response -> assertThat(response).hasStatus(NO_CONTENT)
                                                                                      .hasNoBody());
    }

    @Test
    void shouldHaveWishlistForAuthenticatedUser() {
        // given
        Request<String> request = request(GET,
                                          new Uri("http://www.example.com/wishlist"),
                                          headers(new BearerAuthenticationHeader(new Token("<token>"))));

        UserId userId = new UserId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        given(authenticationService.maybeIdOfAuthenticatedUser(new Token("<token>"), request)).willReturn(Option.of(userId));
        given(wishlistService.wishlistOfUser(userId)).willReturn("<wishlist>");

        // when
        Future<Response<String>> result = router.apply(request);

        // then
        assertThat(result.toTry()).hasValueSatisfying(response -> assertThat(response).hasStatus(OK)
                                                                                      .hasBody("<wishlist>"));
    }

    @Test
    void shouldHaveUnauthorizedForNotAuthenticatedUser() {
        // given
        Request<String> request = request(GET,
                                          new Uri("http://www.example.com/wishlist"),
                                          Headers.empty());

        // when
        Future<Response<String>> result = router.apply(request);

        // then
        assertThat(result.toTry()).hasValueSatisfying(response -> assertThat(response).hasStatus(UNAUTHORIZED));

        then(authenticationService).shouldHaveNoInteractions();
        then(wishlistService).shouldHaveNoInteractions();
    }

    @Test
    void shouldHaveWishlists() {
        // given
        Request<String> request = request(GET,
                                          new Uri("http://www.example.com/wishlists"),
                                          Headers.empty());

        given(wishlistService.allWishlists(request)).willReturn("<wishlists>");

        // when
        Future<Response<String>> result = router.apply(request);

        // then
        assertThat(result.toTry()).hasValueSatisfying(response -> assertThat(response).hasStatus(OK)
                                                                                      .hasBody("<wishlists>"));
    }

    @Test
    void shouldHaveWishlist() {
        // given
        Request<String> request = request(GET,
                                          new Uri("http://www.example.com/wishlists/00000000-0000-0000-0000-000000000003"),
                                          Headers.empty());

        WishlistId wishlistId = new WishlistId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        given(wishlistService.findWishlist(wishlistId)).willReturn(Option.of("<wishlist>"));

        // when
        Future<Response<String>> result = router.apply(request);

        // then
        assertThat(result.toTry()).hasValueSatisfying(response -> assertThat(response).hasStatus(OK)
                                                                                      .hasBody("<wishlist>"));
    }
}