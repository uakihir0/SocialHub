package net.socialhub.mastodon.model;

import net.socialhub.core.model.service.Emoji;
import net.socialhub.core.model.service.Poll;
import net.socialhub.core.model.service.Service;

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
