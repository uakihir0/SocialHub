package net.socialhub.model.service.addition.mastodon;

import mastodon4j.streaming.EventStream;
import net.socialhub.model.error.SocialHubException;
import net.socialhub.model.service.Stream;

import java.io.IOException;

public class MastodonStream implements Stream {

    private EventStream stream;

    public MastodonStream(EventStream stream) {
        this.stream = stream;
    }

    @Override
    public void open() {
        stream.open();
    }

    @Override
    public void close() {
        try {
            stream.close();
        } catch (IOException e) {
            throw new SocialHubException(e);
        }
    }
}
