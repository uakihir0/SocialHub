package net.socialhub.core.utils;

public class HandlingUtil {

    /**
     * Ignore Exceptions
     */
    public static <T> T ignore(Handling<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * To Runtime Error
     */
    public static <T> T runtime(Handling<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface Handling<T> {
        T get() throws Exception;
    }

}
