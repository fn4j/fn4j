package fn4j.http.routing;

import fn4j.net.uri.Path;
import fn4j.net.uri.PathSegment;
import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple0;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

import java.util.function.Function;

import static fn4j.net.uri.Path.EMPTY;

@FunctionalInterface
public interface PathPattern<P> extends Function1<Path, Option<P>> {
    Option<Tuple2<P, Path>> match(Path path);

    @Override
    default Option<P> apply(Path path) {
        return match(path).flatMap(match -> match._2().isEmpty() ? Option.of(match._1()) : Option.none());
    }

    default boolean matches(Path path) {
        return apply(path).isDefined();
    }

    default <Q> PathPattern<Q> map(Function<? super P, ? extends Q> mapper) {
        return path -> match(path).map(match -> match.map1(mapper));
    }

    default <Q> PathPattern<Q> flatMap(Function<? super P, ? extends PathPattern<Q>> mapper) {
        return path -> match(path).flatMap(match -> mapper.apply(match._1()).match(path));
    }

    default PathPattern<P> compile() {
        return this;
    }

    static <P> PathPattern<P> of(Function<? super Path, ? extends Option<Tuple2<P, Path>>> pathPattern) {
        return pathPattern::apply;
    }

    static <P> PathPattern<P> ofFinalizing(Function<? super Path, ? extends Option<P>> pathPattern) {
        return path -> pathPattern.apply(path).map(parameter -> Tuple.of(parameter, EMPTY));
    }

    static NotExtractingPathPattern ofAll(Iterable<? extends PathPattern<?>> pathPatterns) {
        return path -> Stream
                .ofAll(pathPatterns)
                .foldLeft(Option.of(Tuple.of(Tuple.empty(), path)),
                          (maybeRemaining, pathPattern) -> maybeRemaining
                                  .flatMap(remaining -> pathPattern
                                          .match(remaining._2())
                                          .map(match -> match.map1(__ -> remaining._1()))));
    }

    static PathSegmentsPattern0 pathPattern() {
        return new PathSegmentsPattern0(Stream.empty());
    }

    static PathSegmentsPattern0 pathPattern(String pathSegment) {
        return pathPattern(new PathSegment(pathSegment));
    }

    static PathSegmentsPattern0 pathPattern(Path path) {
        return new PathSegmentsPattern0(path.pathSegments().toStream().map(PathSegmentLiteral::new));
    }

    static PathSegmentsPattern0 pathPattern(PathSegment pathSegment) {
        return pathPattern(new PathSegmentLiteral(pathSegment));
    }

    record PathSegmentLiteral(PathSegment pathSegment) implements NotExtractingPathSegmentPattern {
        @Override
        public Option<Tuple0> matchSegment(PathSegment pathSegment) {
            return Option.when(this.pathSegment.equals(pathSegment), Tuple::empty);
        }
    }

    static PathSegmentsPattern0 pathPattern(NotExtractingPathPattern notExtractingPathPattern) {
        return new PathSegmentsPattern0(Stream.of(notExtractingPathPattern));
    }

    static <P1> PathSegmentsPattern1<P1> pathPattern(PathPattern<P1> pathPattern1) {
        return new PathSegmentsPattern1<>(Stream.empty(), pathPattern1, Stream.empty());
    }

    @FunctionalInterface
    interface NotExtractingPathPattern extends PathPattern<Tuple0> {
        @Override
        default NotExtractingPathPattern compile() {
            return this;
        }

        static NotExtractingPathPattern of(Function<? super Path, ? extends Option<Tuple2<Tuple0, Path>>> notExtractingPathPattern) {
            return notExtractingPathPattern::apply;
        }
    }

    @FunctionalInterface
    interface PathSegmentPattern<P> extends PathPattern<P> {
        Option<P> matchSegment(PathSegment pathSegment);

        default <Q> PathSegmentPattern<Q> mapSegment(Function<? super P, ? extends Q> mapper) {
            return pathSegment -> matchSegment(pathSegment).map(mapper);
        }

        default <Q> PathSegmentPattern<Q> flatMapSegment(Function<? super P, ? extends PathSegmentPattern<Q>> mapper) {
            return pathSegment -> matchSegment(pathSegment).flatMap(parameter -> mapper.apply(parameter).matchSegment(pathSegment));
        }

        @Override
        default Option<Tuple2<P, Path>> match(Path path) {
            return path.head().flatMap(firstSegment -> matchSegment(firstSegment).map(parameter -> Tuple.of(parameter, path.tail())));
        }

        static <P> PathSegmentPattern<P> of(Function<? super PathSegment, ? extends Option<P>> pathSegmentPattern) {
            return pathSegmentPattern::apply;
        }
    }

    @FunctionalInterface
    interface NotExtractingPathSegmentPattern extends PathSegmentPattern<Tuple0>, NotExtractingPathPattern {
        static NotExtractingPathSegmentPattern of(Function<? super PathSegment, ? extends Option<Tuple0>> notExtractingPathSegmentPattern) {
            return notExtractingPathSegmentPattern::apply;
        }
    }

    interface PathSegmentsPattern<P> extends PathPattern<P> {
        default PathSegmentsPattern<P> slash(String pathSegment) {
            return slash(new PathSegment(pathSegment));
        }

        default PathSegmentsPattern<P> slash(PathSegment pathSegment) {
            return slash(new PathSegmentLiteral(pathSegment));
        }

        PathSegmentsPattern<P> slash(NotExtractingPathPattern notExtractingPathPattern);
    }

    record PathSegmentsPattern0(Seq<NotExtractingPathPattern> notExtractingPathPatterns0)
            implements PathSegmentsPattern<Tuple0>, NotExtractingPathPattern {

        @Override
        public Option<Tuple2<Tuple0, Path>> match(Path path) {
            return ofAll(notExtractingPathPatterns0).match(path);
        }

        @Override
        public PathSegmentsPattern0 compile() {
            return new PathSegmentsPattern0(notExtractingPathPatterns0.map(NotExtractingPathPattern::compile).toVector());
        }

        @Override
        public PathSegmentsPattern0 slash(String pathSegment) {
            return slash(new PathSegment(pathSegment));
        }

        @Override
        public PathSegmentsPattern0 slash(PathSegment pathSegment) {
            return slash(new PathSegmentLiteral(pathSegment));
        }

        @Override
        public PathSegmentsPattern0 slash(NotExtractingPathPattern notExtractingPathPattern) {
            return new PathSegmentsPattern0(notExtractingPathPatterns0.append(notExtractingPathPattern));
        }

        public <P1> PathSegmentsPattern1<P1> slash(PathPattern<P1> pathPattern1) {
            return new PathSegmentsPattern1<>(notExtractingPathPatterns0,
                                              pathPattern1,
                                              Stream.empty());
        }
    }

    record PathSegmentsPattern1<P1>(Seq<NotExtractingPathPattern> notExtractingPathPatterns0,
                                    PathPattern<P1> pathPattern1,
                                    Seq<NotExtractingPathPattern> notExtractingPathPatterns1) implements PathSegmentsPattern<P1> {
        @Override
        public Option<Tuple2<P1, Path>> match(Path path) {
            return ofAll(notExtractingPathPatterns0)
                    .match(path)
                    .map(Tuple2::_2)
                    .flatMap(remainingPath0 -> pathPattern1
                            .match(remainingPath0)
                            .flatMap(match1 -> ofAll(notExtractingPathPatterns1)
                                    .match(match1._2())
                                    .map(Tuple2::_2)
                                    .map(remainingPath1 -> Tuple.of(match1._1(), remainingPath1))));
        }

        @Override
        public PathSegmentsPattern1<P1> compile() {
            return new PathSegmentsPattern1<>(notExtractingPathPatterns0.map(NotExtractingPathPattern::compile).toVector(),
                                              pathPattern1.compile(),
                                              notExtractingPathPatterns1.map(NotExtractingPathPattern::compile).toVector());
        }

        @Override
        public PathSegmentsPattern1<P1> slash(String pathSegment) {
            return slash(new PathSegment(pathSegment));
        }

        @Override
        public PathSegmentsPattern1<P1> slash(PathSegment pathSegment) {
            return slash(new PathSegmentLiteral(pathSegment));
        }

        @Override
        public PathSegmentsPattern1<P1> slash(NotExtractingPathPattern notExtractingPathPattern) {
            return new PathSegmentsPattern1<>(notExtractingPathPatterns0,
                                              pathPattern1,
                                              notExtractingPathPatterns1.append(notExtractingPathPattern));
        }

        public <P2> PathSegmentsPattern2<P1, P2> slash(PathPattern<P2> pathPattern2) {
            return new PathSegmentsPattern2<>(notExtractingPathPatterns0,
                                              pathPattern1,
                                              notExtractingPathPatterns1,
                                              pathPattern2,
                                              Stream.empty());
        }
    }

    record PathSegmentsPattern2<P1, P2>(Seq<NotExtractingPathPattern> notExtractingPathPatterns0,
                                        PathPattern<P1> pathPattern1,
                                        Seq<NotExtractingPathPattern> notExtractingPathPatterns1,
                                        PathPattern<P2> pathPattern2,
                                        Seq<NotExtractingPathPattern> notExtractingPathPatterns2) implements PathSegmentsPattern<Tuple2<P1, P2>> {
        @Override
        public Option<Tuple2<Tuple2<P1, P2>, Path>> match(Path path) {
            return ofAll(notExtractingPathPatterns0)
                    .match(path)
                    .map(Tuple2::_2)
                    .flatMap(remainingPath0 -> pathPattern1
                            .match(remainingPath0)
                            .flatMap(match1 -> ofAll(notExtractingPathPatterns1)
                                    .match(match1._2())
                                    .map(Tuple2::_2)
                                    .flatMap(remainingPath1 -> pathPattern2
                                            .match(remainingPath1)
                                            .flatMap(match2 -> ofAll(notExtractingPathPatterns2)
                                                    .match(match2._2())
                                                    .map(Tuple2::_2)
                                                    .map(remainingPath2 -> Tuple.of(Tuple.of(match1._1(), match2._1()), remainingPath2))))));
        }

        @Override
        public PathSegmentsPattern2<P1, P2> compile() {
            return new PathSegmentsPattern2<>(notExtractingPathPatterns0.map(NotExtractingPathPattern::compile).toVector(),
                                              pathPattern1.compile(),
                                              notExtractingPathPatterns1.map(NotExtractingPathPattern::compile).toVector(),
                                              pathPattern2.compile(),
                                              notExtractingPathPatterns2.map(NotExtractingPathPattern::compile).toVector());
        }

        @Override
        public PathSegmentsPattern2<P1, P2> slash(String pathSegment) {
            return slash(new PathSegment(pathSegment));
        }

        @Override
        public PathSegmentsPattern2<P1, P2> slash(PathSegment pathSegment) {
            return slash(new PathSegmentLiteral(pathSegment));
        }

        @Override
        public PathSegmentsPattern2<P1, P2> slash(NotExtractingPathPattern notExtractingPathPattern) {
            return new PathSegmentsPattern2<>(notExtractingPathPatterns0,
                                              pathPattern1,
                                              notExtractingPathPatterns1,
                                              pathPattern2,
                                              notExtractingPathPatterns2.append(notExtractingPathPattern));
        }
    }
}