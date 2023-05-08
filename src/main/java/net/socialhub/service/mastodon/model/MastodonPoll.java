package net.socialhub.service.mastodon.model;

import net.socialhub.core.model.Emoji;
import net.socialhub.core.model.Poll;
import net.socialhub.core.model.Service;

import java.util.List;

public class MastodonPoll extends Poll {

    /** emojis which contains in option titles */
    private List<Emoji> emojis;

    public MastodonPoll(Service service) {
        super(service);
    }

    // region
    public List<Emoji> getEmojis() {
        return emojis;
    }

    public void setEmojis(List<Emoji> emojis) {
        this.emojis = emojis;
    }
    // endregion
}
