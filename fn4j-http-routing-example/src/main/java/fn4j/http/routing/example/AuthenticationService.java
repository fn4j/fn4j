package fn4j.http.routing.example;

import fn4j.http.core.Request;
import io.vavr.control.Option;

public interface AuthenticationService {
    Option<UserId> maybeIdOfAuthenticatedUser(String token,
                                              Request<String> request);
}