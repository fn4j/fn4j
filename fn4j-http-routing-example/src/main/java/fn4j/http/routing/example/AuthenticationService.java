package fn4j.http.routing.example;

import fn4j.http.core.Request;
import fn4j.http.core.header.BearerAuthenticationHeader.Token;
import io.vavr.control.Option;

public interface AuthenticationService {
    Option<UserId> maybeIdOfAuthenticatedUser(Token token,
                                              Request<String> request);
}