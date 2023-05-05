package net.socialhub.twitter.define;

import java.util.Arrays;
import java.util.List;

/**
 * Twitter Reaction Type
 * (Action code with alias)
 */
public enum TwitterReactionType {

    Favorite("favorite", "like"),
    Retweet("retweet", "reblog", "share"),
    ;

    private List<String> code;

    TwitterReactionType(String... codes) {
        this.code = Arrays.asList(codes);
    }

    public List<String> getCode() {
        return code;
    }
}
