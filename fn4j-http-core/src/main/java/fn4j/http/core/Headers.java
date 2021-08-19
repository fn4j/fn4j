package fn4j.http.core;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMultimap;
import io.vavr.collection.Multimap;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

import java.util.Iterator;

public interface Headers extends Iterable<Tuple2<HeaderName, HeaderValue>> {
    Multimap<HeaderName, HeaderValue> multimap();

    Stream<Tuple2<HeaderName, HeaderValue>> stream();

    Seq<HeaderValue> get(HeaderName headerName);

    boolean contains(HeaderName headerName);

    boolean contains(Tuple2<HeaderName, HeaderValue> header);

    default boolean contains(HeaderName headerName,
                             HeaderValue headerValue) {
        return contains(Tuple.of(headerName, headerValue));
    }

    Headers add(Tuple2<HeaderName, HeaderValue> header);

    default Headers add(HeaderName headerName,
                        HeaderValue headerValue) {
        return add(Tuple.of(headerName, headerValue));
    }

    Headers remove(HeaderName headerName);

    Headers remove(Tuple2<HeaderName, HeaderValue> header);

    default Headers remove(HeaderName headerName,
                           HeaderValue headerValue) {
        return remove(Tuple.of(headerName, headerValue));
    }

    static Headers empty() {
        return new Immutable(HashMultimap.withSeq().empty());
    }

    @SafeVarargs
    static Headers headers(Tuple2<HeaderName, HeaderValue>... headers) {
        return headers(Stream.of(headers));
    }

    static Headers headers(Iterable<? extends Tuple2<HeaderName, HeaderValue>> headers) {
        return headers(HashMultimap.withSeq().ofEntries(headers));
    }

    static Headers headers(Multimap<HeaderName, HeaderValue> headers) {
        return new Immutable(headers);
    }

    record Immutable(Multimap<HeaderName, HeaderValue> value) implements Headers {
        @Override
        public Multimap<HeaderName, HeaderValue> multimap() {
            return value;
        }

        @Override
        public Stream<Tuple2<HeaderName, HeaderValue>> stream() {
            return value.toStream();
        }

        @Override
        public Stream<HeaderValue> get(HeaderName headerName) {
            return value.get(headerName).fold(Stream::empty, Stream::ofAll);
        }

        @Override
        public boolean contains(HeaderName headerName) {
            return value.containsKey(headerName);
        }

        @Override
        public boolean contains(Tuple2<HeaderName, HeaderValue> header) {
            return value.contains(header);
        }

        @Override
        public Headers add(Tuple2<HeaderName, HeaderValue> header) {
            return new Immutable(value.put(header));
        }

        @Override
        public Headers remove(HeaderName headerName) {
            return new Immutable(value.remove(headerName));
        }

        @Override
        public Headers remove(Tuple2<HeaderName, HeaderValue> header) {
            return new Immutable(header.apply(value::remove));
        }

        @Override
        public Iterator<Tuple2<HeaderName, HeaderValue>> iterator() {
            return value.iterator();
        }
    }
}