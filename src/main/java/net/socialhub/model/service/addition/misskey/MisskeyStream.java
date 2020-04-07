package net.socialhub.model.service.addition.misskey;

import net.socialhub.model.service.Stream;

import java.util.function.Consumer;

public class MisskeyStream implements Stream {

    private misskey4j.stream.MisskeyStream stream;
    private Consumer<misskey4j.stream.MisskeyStream> start;

    public MisskeyStream(
            misskey4j.stream.MisskeyStream stream,
            Consumer<misskey4j.stream.MisskeyStream> start) {
        this.stream = stream;
        this.start = start;
    }

    @Override
    public void open() {
        stream.connectBlocking();
        start.accept(stream);
    }

    @Override
    public void close() {
        stream.close();
    }
}
