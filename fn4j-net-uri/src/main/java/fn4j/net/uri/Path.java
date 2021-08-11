package fn4j.net.uri;

import io.vavr.API;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

import static fn4j.net.uri.Literal.SLASH;
import static io.vavr.API.Seq;

public record Path(Seq<PathSegment> pathSegments) implements UriComponentParent {
    public static final Path EMPTY = new Path(Seq());

    public Path(PathSegment... pathSegments) {
        this(Stream.of(pathSegments));
    }

    public Path(Iterable<? extends PathSegment> pathSegments) {
        this(Stream.ofAll(pathSegments));
    }

    public Path(String maybeValue) {
        this(Option.of(maybeValue)
                   .<Seq<String>>fold(API::Seq,
                                      value -> Stream.of(value.split("/"))
                                                     .filter(segmentValue -> !segmentValue.isEmpty())
                                                     .toList())
                   .map(PathSegment::new));
    }

    public static Path concat(Path... paths) {
        return concat(Stream.of(paths));
    }

    private static Path concat(Iterable<? extends Path> paths) {
        return new Path(Stream.ofAll(paths).flatMap(Path::pathSegments));
    }

    public int length() {
        return pathSegments.length();
    }

    public boolean isEmpty() {
        return pathSegments.isEmpty();
    }

    public Path prepend(PathSegment pathSegment) {
        return new Path(pathSegments.prepend(pathSegment));
    }

    public Path prependAll(Iterable<? extends PathSegment> pathSegments) {
        return new Path(this.pathSegments.prependAll(pathSegments));
    }

    public Path append(PathSegment pathSegment) {
        return new Path(pathSegments.append(pathSegment));
    }

    public Path appendAll(Iterable<? extends PathSegment> pathSegments) {
        return new Path(this.pathSegments.appendAll(pathSegments));
    }

    public Path drop(int n) {
        return new Path(pathSegments.drop(n));
    }

    public Option<PathSegment> head() {
        return pathSegments.headOption();
    }

    public Path tail() {
        return drop(1);
    }

    public Option<PathSegment> slug() {
        return pathSegments.lastOption();
    }

    public Option<PathSegment> get(int index) {
        return drop(index).head();
    }

    public boolean startsWith(Path path) {
        return pathSegments.startsWith(path.pathSegments);
    }

    public Option<Path> without(Path path) {
        return Option.when(startsWith(path),
                           drop(path.length()));
    }

    @Override
    public Seq<UriComponent> components() {
        return Stream.<UriComponent>narrow(pathSegments.toStream())
                     .intersperse(SLASH)
                     .prepend(SLASH);
    }
}