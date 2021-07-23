package io.vavr.collection;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Sets {

    public static <T> LinkedHashSet<T> addOrUpdate(LinkedHashSet<T> linkedHashSet,
                                                   Predicate<T> predicate,
                                                   Consumer<T> consumer,
                                                   Supplier<T> supplier) {
        return _addOrUpdate(linkedHashSet, predicate, consumer, supplier);
    }

    /**
     * The cast is safe, because all {@link io.vavr.collection.Set}
     * implementations within Vavr return the same implementation type.
     */
    @SuppressWarnings("unchecked")
    private static <T, S extends Set<T>> S _addOrUpdate(S set,
                                                        Predicate<T> predicate,
                                                        Consumer<T> consumer,
                                                        Supplier<T> supplier) {
        return set.find(predicate)
                  .fold(() -> (S) set.add(supplier.get()),
                        element -> {
                            consumer.accept(element);
                            return set;
                        });
    }

    private Sets() {
    }
}
