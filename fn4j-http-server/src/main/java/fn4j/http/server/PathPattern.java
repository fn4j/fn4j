package fn4j.http.server;

import fn4j.http.core.Path;
import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple0;
import io.vavr.control.Option;

import static fn4j.http.core.Path.ROOT;

@FunctionalInterface
public interface PathPattern<P> extends Function1<Path, Option<P>> {
    PathPattern<Tuple0> Root = exact(ROOT);

    @Override
    default Option<P> apply(Path path) {
        return matches(path) instanceof MatchResult.Complete<P> complete ?
                Option.of(complete.parameter()) :
                Option.none();
    }

    MatchResult<P> matches(Path path);

    default PathPattern<P> slash(String remainder) {
        return slash(new Path(this == Root ? remainder : "/" + remainder));
    }

    default PathPattern<P> slash(Path element) {
        return path -> matches(path) instanceof MatchResult.Partial<P> partial ?
                exact(element).matches(partial.remainder())
                              .withParameter(partial.parameter())
                : new MatchResult.Failed<P>();
    }

    static PathPattern<Tuple0> exact(Path path) {
        return actualPath -> actualPath.equals(path) ?
                new MatchResult.Complete<>(Tuple.empty()) :
                actualPath.value().startsWith(path.value()) ?
                        new MatchResult.Partial<>(Tuple.empty(), new Path(actualPath.value().substring(path.value().length()))) :
                        new MatchResult.Failed<>();
    }

    interface MatchResult<P> {
        <Q> MatchResult<Q> withParameter(Q parameter);

        record Complete<P>(P parameter) implements MatchResult<P> {
            @Override
            public <Q> Complete<Q> withParameter(Q parameter) {
                return new Complete<>(parameter);
            }
        }

        record Partial<P>(P parameter, Path remainder) implements MatchResult<P> {
            @Override
            public <Q> Partial<Q> withParameter(Q parameter) {
                return new Partial<>(parameter, remainder);
            }
        }

        record Failed<P>() implements MatchResult<P> {
            @Override
            @SuppressWarnings("unchecked")
            public <Q> Failed<Q> withParameter(Q parameter) {
                return (Failed<Q>) this;
            }
        }
    }
}