package fn4j.validation;

import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

public interface Violation {
    Seq<Movement> movements();

    Key key();

    static Violation violation(final Key key) {
        return new Root(key);
    }

    record Root(Key key) implements Violation {
        @Override
        public Seq<Movement> movements() {
            return Stream.empty();
        }
    }

    record Key(String value) {
    }
}