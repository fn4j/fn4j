package fn4j.http.routing.example;

import fn4j.http.routing.Route;
import io.vavr.collection.Seq;

import static fn4j.http.routing.Route.route;

public record AdminController(UsersController usersController) {
    public Seq<Route<?, String, String>> routes() {
        return route("admin", usersController.routes());
    }
}