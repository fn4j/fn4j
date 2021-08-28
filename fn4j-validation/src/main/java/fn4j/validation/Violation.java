package fn4j.validation;

import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

import java.util.function.Function;

public interface Violation {
    Key key();

    Seq<Movement> movements();

    default String movementsString() {
        return movements().toStream().map(Movement::value).mkString(".");
    }

    Violation mapMovements(Function<? super Seq<Movement>, ? extends Seq<Movement>> mapper);

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
                          Seq<Movement> movements) implements Violation {
        @Override
        public Violation mapMovements(Function<? super Seq<Movement>, ? extends Seq<Movement>> mapper) {
            return new PlainViolation(key, mapper.apply(movements));
        }
    }

    record ThrowableViolation<T extends Throwable>(Key key,
                                                   Seq<Movement> movements,
                                                   T throwable) implements Violation {
        @Override
        public ThrowableViolation<T> mapMovements(Function<? super Seq<Movement>, ? extends Seq<Movement>> mapper) {
            return new ThrowableViolation<>(key, mapper.apply(movements), throwable);
        }
    }

    record Key(String value) {
    }
}