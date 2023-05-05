package net.socialhub.mastodon.model;

import mastodon4j.streaming.EventStream;
import net.socialhub.core.model.error.SocialHubException;
import net.socialhub.core.model.service.Stream;

import java.io.IOException;

public class MastodonStream implements Stream {

    private boolean connecting = false;
    private EventStream stream;

    /** Stream は後で設定 */
    public void setStream(EventStream stream) {
        this.stream = stream;
    }

    public void setConnecting(boolean connecting) {
        this.connecting = connecting;
    }

    @Override
    public void open() {
        stream.open();
        connecting = true;
    }

    @Override
    public void close() {
        try {
            stream.close();
            connecting = false;

        } catch (IOException e) {
            connecting = false;
            throw new SocialHubException(e);
        }
    }

    @Override
    public boolean isOpened() {
        return connecting;
    }
}
