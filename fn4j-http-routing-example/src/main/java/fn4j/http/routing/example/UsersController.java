package fn4j.http.routing.example;

import fn4j.http.answering.Handler;
import fn4j.http.core.Body;
import fn4j.http.core.Request;
import fn4j.http.core.header.Headers;
import fn4j.http.core.header.LocationHeader;
import fn4j.http.routing.PathPattern.PathSegmentPattern;
import fn4j.http.routing.Route;
import fn4j.net.uri.Path;
import fn4j.net.uri.Uri;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.function.Function;

import static fn4j.http.core.Method.*;
import static fn4j.http.core.Response.response;
import static fn4j.http.core.Status.*;
import static fn4j.http.core.header.Headers.headers;
import static fn4j.http.routing.PathPatterns.uuidTry;
import static fn4j.http.routing.Route.route;
import static io.vavr.API.TODO;

public record UsersController(UserService userService) {
    public static final PathSegmentPattern<Try<UserId>> USER_ID = uuidTry().mapSegment(maybeUuid -> maybeUuid.map(UserId::new));

    public Seq<Route<?, String, String>> routes() {
        return route("users", Stream.of(route(GET, getUsers()),
                                        route(POST, postUser()),
                                        route(GET, USER_ID, getUser()),
                                        route(PUT, USER_ID, putUser()),
                                        route(DELETE, USER_ID, deleteUser())));
    }

    private Handler<String, String> getUsers() {
        return request -> {
            var allUsers = userService.allUsers(request);
            return Future.successful(response(OK, Headers.empty(), new Body<>(allUsers)));
        };
    }

    private Handler<String, String> postUser() {
        return request -> {
            var user = userService.createUser(request);
            var userLocation = userLocation(user, request);
            var locationHeader = new LocationHeader(userLocation);
            return Future.successful(response(CREATED, headers(locationHeader)));
        };
    }

    private Function<Try<UserId>, Handler<String, String>> getUser() {
        return maybeUserId -> request -> maybeUserId.fold(error -> {
            return Future.successful(response(BAD_REQUEST, Headers.empty()));
        }, userId -> {
            var maybeUser = userService.findUser(userId, request);
            return maybeUser.fold(() -> {
                return Future.successful(response(NOT_FOUND, Headers.empty()));
            }, user -> {
                return Future.successful(response(OK, Headers.empty(), new Body<>(maybeUser.get())));
            });
        });
    }

    private Function<Try<UserId>, Handler<String, String>> putUser() {
        return maybeUserId -> request -> maybeUserId.fold(error -> TODO("invalid user id -> bad request"), userId -> {
            var maybeSuccessfulUpdateIndicator = userService.updateUser(userId, request);
            return maybeSuccessfulUpdateIndicator.fold(() -> TODO("none -> user not found"), successfulUpdateIndicator -> {
                return Future.successful(response(NO_CONTENT, Headers.empty()));
            });
        });
    }

    private Function<Try<UserId>, Handler<String, String>> deleteUser() {
        return maybeUserId -> request -> maybeUserId.fold(error -> TODO("invalid user id -> bad request"), userId -> {
            var maybeSuccessfulDeletionIndicator = userService.deleteUser(userId, request);
            return maybeSuccessfulDeletionIndicator.fold(() -> TODO("none -> user not found"), successfulDeletionIndicator -> {
                return Future.successful(response(NO_CONTENT, Headers.empty()));
            });
        });
    }

    private Uri userLocation(String user,
                             Request<String> request) {
        return new Uri(request.uri().maybeScheme(),
                       request.uri().maybeAuthority(),
                       new Path("/admin/users/" + user),
                       Option.none(),
                       Option.none());
    }
}