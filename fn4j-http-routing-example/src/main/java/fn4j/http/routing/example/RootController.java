package fn4j.http.routing.example;

import fn4j.http.routing.Route;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

public record RootController(AdminController adminController,
                             WishlistController wishlistController) {
    public Seq<Route<?, String, String>> routes() {
        return Stream.concat(adminController.routes(),
                             wishlistController.routes());
    }
}