package net.socialhub.service.mastodon.define;

import java.util.Arrays;
import java.util.List;

/**
 * Mastodon Reaction Type
 * (Action code with alias)
 */
public enum MastodonReactionType {

    Favorite("favorite", "like"),
    Reblog("reblog", "retweet", "share"),
    Reply("reply"),
    ;

    private List<String> code;

    MastodonReactionType(String... codes) {
        this.code = Arrays.asList(codes);
    }

    public List<String> getCode() {
        return code;
    }
}
