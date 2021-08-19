package fn4j.http.routing.example;

import fn4j.http.core.Request;
import io.vavr.Tuple0;
import io.vavr.control.Option;

public interface UserService {
    String allUsers(Request<String> request);

    String createUser(Request<String> request);

    Option<String> findUser(UserId userId,
                            Request<String> request);

    Option<Tuple0> updateUser(UserId userId,
                              Request<String> request);

    Option<Tuple0> deleteUser(UserId userId,
                              Request<String> request);
}