package net.socialhub.model.service.addition.twitter;

import net.socialhub.model.service.Stream;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TwitterStream implements Stream {

    private twitter4j.TwitterStream streaming = null;
    private Supplier<twitter4j.TwitterStream> stream;
    private Consumer<twitter4j.TwitterStream> open;

    public TwitterStream(
            Supplier<twitter4j.TwitterStream> stream,
            Consumer<twitter4j.TwitterStream> open) {
        this.stream = stream;
        this.open = open;
    }

    @Override
    public void open() {
        if (streaming != null) {
            close();
        }
        streaming = stream.get();
        open.accept(streaming);
    }

    @Override
    public void close() {
        if (streaming != null) {
            streaming.shutdown();
            streaming = null;
        }
    }

    @Override
    public boolean isOpened() {
        return streaming != null;
    }
}
