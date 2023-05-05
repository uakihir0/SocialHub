package net.socialhub.misskey.model;

import net.socialhub.core.model.service.Stream;

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
