package fn4j.http.routing;

import fn4j.http.routing.PathPattern.NotExtractingPathSegmentPattern;
import fn4j.http.routing.PathPattern.PathSegmentPattern;
import fn4j.net.uri.Path;
import io.vavr.Tuple;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.math.BigInteger;
import java.util.UUID;
import java.util.function.Function;

import static fn4j.net.uri.Path.EMPTY;

public interface PathPatterns {

    static PathPattern<Path> wildcard() {
        return path -> Option.of(Tuple.of(path, EMPTY));
    }

    static NotExtractingPathSegmentPattern anySegment() {
        return __ -> Option.of(Tuple.empty());
    }

    static PathSegmentPattern<String> string() {
        return pathSegment -> Option.of(pathSegment.value());
    }

    static <P> PathSegmentPattern<Try<P>> tryString(Function<String, ? extends P> mapper) {
        return string().mapSegment(string -> Try.of(() -> mapper.apply(string)));
    }

    static <P> PathSegmentPattern<P> doNotMatchOnError(PathSegmentPattern<Try<P>> tryPathSegmentPattern) {
        return tryPathSegmentPattern.flatMapSegment(try$ -> __ -> try$.toOption());
    }

    static PathSegmentPattern<Try<BigInteger>> bigIntegerTry() {
        return tryString(BigInteger::new);
    }

    static PathSegmentPattern<BigInteger> bigInteger() {
        return doNotMatchOnError(bigIntegerTry());
    }

    static PathSegmentPattern<Try<UUID>> uuidTry() {
        return tryString(UUID::fromString);
    }

    static PathSegmentPattern<UUID> uuid() {
        return doNotMatchOnError(uuidTry());
    }
}