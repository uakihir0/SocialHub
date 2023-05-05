package net.socialhub.core.define;

import java.util.Arrays;
import java.util.List;

public enum ReactionType {

    // List aliases of mainly actions.
    // 代表的なリアクションのエイリアス登録

    Like("like", "favorite"),
    Share("share", "retweet", "reblog"),
    Reply("reply"),
    ;

    private List<String> code;

    ReactionType(String... codes) {
        this.code = Arrays.asList(codes);
    }

    public List<String> getCode() {
        return code;
    }
}
