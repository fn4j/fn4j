package fn4j.validation;

import io.vavr.collection.Seq;
import io.vavr.control.Either;

import java.util.Iterator;

public interface Validation<A> extends Iterable<A> {
    Either<? extends Seq<Violation>, A> toEither();

    @Override
    default Iterator<A> iterator() {
        return toEither().iterator();
    }
}