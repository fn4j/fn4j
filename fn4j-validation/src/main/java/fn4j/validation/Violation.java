package fn4j.validation;

import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

import java.util.function.Function;

public interface Violation {
    Seq<Movement> movements();

    Key key();

    Violation mapMovements(Function<? super Seq<Movement>, ? extends Seq<Movement>> mapper);

    static Violation violation(final Key key) {
        return violation(Stream.empty(), key);
    }

    static Violation violation(Stream<Movement> movements,
                               final Key key) {
        return new Immutable(movements, key);
    }

    static Key key(String value) {
        return new Key(value);
    }

    record Immutable(Seq<Movement> movements,
                     Key key) implements Violation {
        @Override
        public Violation mapMovements(Function<? super Seq<Movement>, ? extends Seq<Movement>> mapper) {
            return new Immutable(mapper.apply(movements), key);
        }
    }

    record Key(String value) {
    }
}