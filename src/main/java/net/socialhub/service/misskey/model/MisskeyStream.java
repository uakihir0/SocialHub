package net.socialhub.service.misskey.model;

import net.socialhub.core.model.Stream;

public class MisskeyStream implements Stream {

    private misskey4j.stream.MisskeyStream stream;

    public MisskeyStream(
            misskey4j.stream.MisskeyStream stream) {
        this.stream = stream;
    }

    @Override
    public void open() {
        stream.connect();
    }

    @Override
    public void close() {
        stream.close();
    }

    @Override
    public boolean isOpened() {
        return stream.isOpen();
    }
}
