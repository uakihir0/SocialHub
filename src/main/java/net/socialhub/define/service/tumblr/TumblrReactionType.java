package net.socialhub.define.service.tumblr;

import java.util.Arrays;
import java.util.List;

/**
 * Tumblr Reaction Type
 * (Action code with alias)
 */
public enum TumblrReactionType {

    Like("like", "favorite"),
    Reblog("reblog", "retweet", "share"),
    Reply("reply"),
    ;

    private List<String> code;

    TumblrReactionType(String... codes) {
        this.code = Arrays.asList(codes);
    }

    public List<String> getCode() {
        return code;
    }
}

