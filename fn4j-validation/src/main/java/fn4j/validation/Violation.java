package fn4j.validation;

import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

import java.util.function.Function;

public interface Violation {
    Key key();

    Seq<String> path();

    Violation mapPath(Function<? super Seq<String>, ? extends Seq<String>> mapper);

    static Violation violation(Key key) {
        return new PlainViolation(key, Stream.empty());
    }

    static <T extends Throwable> Violation violation(Key key,
                                                     T throwable) {
        return new ThrowableViolation<>(key, Stream.empty(), throwable);
    }

    static Key key(String value) {
        return new Key(value);
    }

    record PlainViolation(Key key,
                          Seq<String> path) implements Violation {
        @Override
        public Violation mapPath(Function<? super Seq<String>, ? extends Seq<String>> mapper) {
            return new PlainViolation(key, mapper.apply(path));
        }
    }

    record ThrowableViolation<T extends Throwable>(Key key,
                                                   Seq<String> path,
                                                   T throwable) implements Violation {
        @Override
        public ThrowableViolation<T> mapPath(Function<? super Seq<String>, ? extends Seq<String>> mapper) {
            return new ThrowableViolation<>(key, mapper.apply(path), throwable);
        }
    }

    record Key(String value) {
    }
}