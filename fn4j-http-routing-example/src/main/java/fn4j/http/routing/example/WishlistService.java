package fn4j.http.routing.example;

import fn4j.http.core.Request;
import io.vavr.control.Option;

public interface WishlistService {
    String wishlistOfUser(UserId userId);

    String allWishlists(Request<String> request);

    Option<String> findWishlist(WishlistId wishlistId);
}