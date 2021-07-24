package fn4j.control;

import io.vavr.control.Either;
import io.vavr.control.Option;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Results {
    public static <E, O> Result<E, O> result(Either<E, O> either) {
        return either.fold(Result::error, Result::ok);
    }

    public static <E, O> Result<E, O> result(Option<O> option, Supplier<E> errorSupplier) {
        return option.fold(() -> Result.error(errorSupplier.get()), Result::ok);
    }

    public static <E, O, O1, O2> Result<E, O> flatMap(Result<E, O1> result1,
                                                      Result<E, O2> result2,
                                                      BiFunction<O1, O2, Result<E, O>> mapper) {
        return result1.flatMap(o1 -> result2.flatMap(o2 -> mapper.apply(o1, o2)));
    }

    public static <E, O, O1, O2> Result<E, O> map(Result<E, O1> result1,
                                                  Result<E, O2> result2,
                                                  BiFunction<O1, O2, O> mapper) {
        return result1.flatMap(o1 -> result2.map(o2 -> mapper.apply(o1, o2)));
    }

    private Results() {
    }
}