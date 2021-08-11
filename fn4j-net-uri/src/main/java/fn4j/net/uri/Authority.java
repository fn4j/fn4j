package fn4j.net.uri;

import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

import static fn4j.net.uri.Literal.AT;
import static fn4j.net.uri.Literal.COLON;

public record Authority(Option<UserInfo> maybeUserInfo,
                        Host host,
                        Option<Port> maybePort) implements UriComponentParent {
    @Override
    public Seq<UriComponent> components() {
        return maybeUserInfo.toStream().flatMap(userInfo -> Stream.<UriComponent>of(userInfo, AT))
                            .append(host)
                            .appendAll(maybePort.toStream().flatMap(port -> Stream.<UriComponent>of(COLON, port)));
    }
}