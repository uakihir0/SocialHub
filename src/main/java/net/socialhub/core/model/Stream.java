package net.socialhub.core.model;

/**
 * Stream
 * ストリーム操作 API
 */
public interface Stream {

    void open();

    void close();

    boolean isOpened();
}
