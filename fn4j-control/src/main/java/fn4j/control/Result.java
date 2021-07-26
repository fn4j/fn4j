package fn4j.control;

import io.vavr.Value;
import io.vavr.collection.Iterator;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

    static <E, O> Result<E, Seq<O>> sequenceOk(Iterable<? extends Result<? extends E, ? extends O>> results) {
        return sequenceOk(results, Vector::empty);
    }

    static <E, O> Result<E, Seq<O>> sequenceOk(Iterable<? extends Result<? extends E, ? extends O>> results,
                                               Supplier<Seq<O>> empty) {
        Seq<O> okValues = empty.get();
        for (Result<? extends E, ? extends O> result : results) {
            if (result.isOk()) {
                okValues = okValues.append(result.get());
            } else {
                return error(result.getError());
            }
        }
        return ok(okValues);
    }

    boolean isOk();

    boolean isError();

    @Override
    default O get() {
        return getOk();
    }

    O getOk();

    E getError();

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
        return peekOk(action);
    }

    default Result<E, O> peekOk(Consumer<? super O> action) {
        forEachOk(action);
        return this;
    }

    default Result<E, O> peekError(Consumer<? super E> action) {
        forEachError(action);
        return this;
    }

    default Result<E, O> biPeek(Consumer<? super E> errorAction,
                                Consumer<? super O> okAction) {
        biForEach(errorAction, okAction);
        return this;
    }

    default <U> Result<E, U> flatMap(Function<? super O, ? extends Result<E, ? extends U>> mapper) {
        return flatMapOk(mapper);
    }

    @SuppressWarnings("unchecked")
    default <U> Result<E, U> flatMapOk(Function<? super O, ? extends Result<E, ? extends U>> mapper) {
        if (isOk()) {
            return (Result<E, U>) mapper.apply(get());
        } else {
            return (Result<E, U>) this;
        }
    }

    @SuppressWarnings("unchecked")
    default <F> Result<F, O> flatMapError(Function<? super E, ? extends Result<? extends F, ? extends O>> mapper) {
        if (isOk()) {
            return (Result<F, O>) this;
        } else {
            return (Result<F, O>) mapper.apply(getError());
        }
    }

    @SuppressWarnings("unchecked")
    default <U> Result<E, U> recover(Function<? super E, ? extends U> mapper) {
        if (isOk()) {
            return (Result<E, U>) this;
        } else {
            return ok(mapper.apply(getError()));
        }
    }

    @SuppressWarnings("unchecked")
    default <F> Result<F, O> toError(Function<? super O, ? extends F> mapper) {
        if (isOk()) {
            return error(mapper.apply(get()));
        } else {
            return (Result<F, O>) this;
        }
    }

    default <F> Result<F, O> recoverWith(Function<? super E, ? extends Result<? extends F, ? extends O>> mapper) {
        return flatMapError(mapper);
    }

    @Override
    default <U> Result<E, U> map(Function<? super O, ? extends U> mapper) {
        return mapOk(mapper);
    }

    @SuppressWarnings("unchecked")
    default <U> Result<E, U> mapOk(Function<? super O, ? extends U> mapper) {
        if (isOk()) {
            return ok(mapper.apply(get()));
        } else {
            return (Result<E, U>) this;
        }
    }

    @SuppressWarnings("unchecked")
    default <F> Result<F, O> mapError(Function<? super E, ? extends F> mapper) {
        if (isOk()) {
            return (Result<F, O>) this;
        } else {
            return error(mapper.apply(getError()));
        }
    }

    default <F, U> Result<F, U> biMap(Function<? super E, ? extends F> errorMapper,
                                      Function<? super O, ? extends U> okMapper) {
        if (isOk()) {
            return ok(okMapper.apply(get()));
        } else {
            return error(errorMapper.apply(getError()));
        }
    }

    @Override
    default void forEach(Consumer<? super O> action) {
        forEachOk(action);
    }

    default void forEachOk(Consumer<? super O> action) {
        if (isOk()) {
            action.accept(get());
        }
    }

    default void forEachError(Consumer<? super E> action) {
        if (isError()) {
            action.accept(getError());
        }
    }

    default void biForEach(Consumer<? super E> errorAction,
                           Consumer<? super O> okAction) {
        if (isOk()) {
            okAction.accept(get());
        } else {
            errorAction.accept(getError());
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
        public O getOk() {
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
        public O getOk() {
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