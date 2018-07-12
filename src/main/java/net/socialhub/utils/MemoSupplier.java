package net.socialhub.utils;

import java.util.function.Supplier;

public class MemoSupplier<T> {

    private Supplier<T> supplier;
    private T cache;

    public static <K> MemoSupplier<K> of(Supplier<K> supplier) {
        MemoSupplier<K> model = new MemoSupplier<>();
        model.supplier = supplier;
        model.cache = null;
        return model;
    }

    public T get() {
        if (cache != null) {
            return cache;
        }
        T model = supplier.get();
        this.cache = model;
        return model;
    }
}
