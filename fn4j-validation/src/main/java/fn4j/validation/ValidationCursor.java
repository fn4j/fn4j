package fn4j.validation;

import io.vavr.collection.Seq;
import io.vavr.collection.Stream;

public interface ValidationCursor<A> {
    A value();

    Seq<Movement> movements();

    record Root<A>(A value) implements ValidationCursor<A> {
        @Override
        public Seq<Movement> movements() {
            return Stream.empty();
        }
    }
}