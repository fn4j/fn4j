package fn4j.control;

import io.vavr.Value;
import io.vavr.collection.Iterator;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Result<E, O> extends Value<O> {
    static <E, O> Result<E, O> ok(O ok) {
        return new Ok<>(ok);
    }

    static <E, O> Result<E, O> error(E error) {
        return new Error<>(error);
    }

    @SuppressWarnings("unchecked")
    static <E, O> Result<E, O> narrow(Result<? extends E, ? extends O> result) {
        return (Result<E, O>) result;
    }

    boolean isOk();

    boolean isError();

    E getError();

    static <E, O> Result<E, Seq<O>> sequenceOk(Iterable<? extends Result<? extends E, ? extends O>> results) {
        Vector<O> okValues = Vector.empty();
        for (Result<? extends E, ? extends O> result : results) {
            if (result.isOk()) {
                okValues = okValues.append(result.get());
            } else {
                return error(result.getError());
            }
        }
        return ok(okValues);
    }

    @Override
    default boolean isAsync() {
        return false;
    }

    @Override
    default boolean isLazy() {
        return false;
    }

    @Override
    default boolean isSingleValued() {
        return true;
    }

    @Override
    default Iterator<O> iterator() {
        if (isOk()) {
            return Iterator.of(get());
        } else {
            return Iterator.empty();
        }
    }

    @Override
    default boolean isEmpty() {
        return isError();
    }

    @Override
    default Result<E, O> peek(Consumer<? super O> action) {
        if (isOk()) {
            action.accept(get());
        }
        return this;
    }

    // TODO: peekOk
    // TODO: peekError

    @SuppressWarnings("unchecked")
    default <U> Result<E, U> flatMap(Function<? super O, ? extends Result<E, ? extends U>> mapper) {
        if (isOk()) {
            return (Result<E, U>) mapper.apply(get());
        } else {
            return (Result<E, U>) this;
        }
    }

    // TODO: flatMapOk
    // TODO: flatMapError

    // TODO: recover
    // TODO: break?
    // TODO: recoverWith

    @Override
    @SuppressWarnings("unchecked")
    default <U> Result<E, U> map(Function<? super O, ? extends U> mapper) {
        if (isOk()) {
            return ok(mapper.apply(get()));
        } else {
            return (Result<E, U>) this;
        }
    }

    // TODO: mapOk

    @SuppressWarnings("unchecked")
    default <F> Result<F, O> mapError(Function<? super E, ? extends F> mapper) {
        if (isOk()) {
            return (Result<F, O>) this;
        } else {
            return error(mapper.apply(getError()));
        }
    }

    default <F, U> Result<F, U> bimap(Function<? super E, ? extends F> errorMapper,
                                      Function<? super O, ? extends U> okMapper) {
        if (isOk()) {
            return ok(okMapper.apply(get()));
        } else {
            return error(errorMapper.apply(getError()));
        }
    }

    // TODO: forEachOk
    // TODO: forEachError
    // TODO: biForEach

    default void biforeach(Consumer<? super E> errorConsumer,
                           Consumer<? super O> okConsumer) {
        if (isOk()) {
            okConsumer.accept(get());
        } else {
            errorConsumer.accept(getError());
        }
    }

    default <X> X fold(Function<? super E, ? extends X> ifError,
                       Function<? super O, ? extends X> ifOk) {
        if (isOk()) {
            return ifOk.apply(get());
        } else {
            return ifError.apply(getError());
        }
    }

    final class Ok<E, O> implements Result<E, O> {
        private final O value;

        private Ok(O value) {
            this.value = value;
        }

        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public O get() {
            return value;
        }

        @Override
        public E getError() {
            throw new NoSuchElementException("getError() on Ok");
        }

        @Override
        public String stringPrefix() {
            return "Ok";
        }

        @Override
        public String toString() {
            return stringPrefix() + "(" + value + ")";
        }
    }

    final class Error<E, O> implements Result<E, O> {
        private final E value;

        private Error(E value) {
            this.value = value;
        }

        @Override
        public boolean isOk() {
            return false;
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public O get() {
            throw new NoSuchElementException("get() on Error");
        }

        @Override
        public E getError() {
            return value;
        }

        @Override
        public String stringPrefix() {
            return "Error";
        }
    }
}