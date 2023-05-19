package net.socialhub.service.bluesky.define;

import java.util.Arrays;
import java.util.List;

/**
 * Mastodon Reaction Type
 * (Action code with alias)
 */
public enum BlueskyReactionType {

    Like("like", "favorite"),
    Repost("repost", "retweet", "share"),
    Reply("reply"),
    ;

    private final List<String> code;

    BlueskyReactionType(String... codes) {
        this.code = Arrays.asList(codes);
    }

    public List<String> getCode() {
        return code;
    }
}
