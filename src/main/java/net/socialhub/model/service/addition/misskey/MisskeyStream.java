package net.socialhub.model.service.addition.misskey;

import net.socialhub.model.service.Stream;

public class MisskeyStream implements Stream {

    private misskey4j.stream.MisskeyStream stream;

    public MisskeyStream(
            misskey4j.stream.MisskeyStream stream) {
        this.stream = stream;
    }

    @Override
    public void open() {
        if (!stream.isOpen()) {
            stream.connectBlocking();
        }
    }

    @Override
    public void close() {
        if (stream.isOpen()) {
            stream.close();
        }
    }
}
