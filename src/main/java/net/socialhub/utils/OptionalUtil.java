package net.socialhub.utils;

import java.util.function.Supplier;

public class OptionalUtil {

    /**
     * Ignore Exceptions
     */
    public static <T> T ignore(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception ignore) {
            return null;
        }
    }
}
