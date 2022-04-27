package fn4j.http.core.header;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMultimap;
import io.vavr.collection.Multimap;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

import java.util.Iterator;
import java.util.function.BiConsumer;

public interface Headers extends Iterable<Tuple2<HeaderName, HeaderValue>> {
    Multimap<HeaderName, HeaderValue> multimap();

    Stream<? extends Header> stream();

    default void forEach(BiConsumer<HeaderName, HeaderValue> action) {
        stream().forEach(header -> action.accept(header.headerName(),
                                                 header.headerValue()));
    }

    Seq<Header> get(HeaderName headerName);

    default <A> Seq<A> get(HeaderReader<A> headerReader) {
        return get(headerReader.headerName()).flatMap(header -> headerReader.apply(header.headerValue()));
    }

    default <A> Option<A> getSingle(HeaderReader<A> headerReader) {
        return get(headerReader).singleOption();
    }

    boolean contains(HeaderName headerName);

    boolean contains(Tuple2<HeaderName, HeaderValue> header);

    default boolean contains(Header header) {
        return contains(header.tuple());
    }

    default boolean contains(HeaderName headerName,
                             HeaderValue headerValue) {
        return contains(Tuple.of(headerName, headerValue));
    }

    Headers add(Tuple2<HeaderName, HeaderValue> header);

    default Headers add(Header header) {
        return add(header.tuple());
    }

    default Headers add(HeaderName headerName,
                        HeaderValue headerValue) {
        return add(Tuple.of(headerName, headerValue));
    }

    Headers remove(HeaderName headerName);

    Headers remove(Tuple2<HeaderName, HeaderValue> header);

    default Headers remove(Header header) {
        return remove(header.tuple());
    }

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

    static Headers headers(Header... headers) {
        return headers(Stream.of(headers).map(Header::tuple));
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
        public Stream<? extends Header> stream() {
            return value.toStream().map(RawHeader::new);
        }

        @Override
        public Stream<Header> get(HeaderName headerName) {
            return value.get(headerName)
                        .<Stream<HeaderValue>>fold(Stream::empty, Stream::ofAll)
                        .map(headerValue -> new RawHeader(headerName, headerValue));
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