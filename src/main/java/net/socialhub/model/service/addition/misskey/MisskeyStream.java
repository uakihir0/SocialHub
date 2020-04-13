package net.socialhub.model.service.addition.misskey;

import net.socialhub.model.service.Stream;

public class MisskeyStream implements Stream {

    private misskey4j.stream.MisskeyStream stream;
    private Runnable runnable;


    public MisskeyStream(
            misskey4j.stream.MisskeyStream stream,
            Runnable runnable) {
        this.stream = stream;
        this.runnable = runnable;
    }

    @Override
    public void open() {
        if (!stream.isOpen()) {
            stream.connectBlocking();
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    @Override
    public void close() {
        if (stream.isOpen()) {
            stream.close();
        }
    }
}
