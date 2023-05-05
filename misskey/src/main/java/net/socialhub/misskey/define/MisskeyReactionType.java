package net.socialhub.misskey.define;

import java.util.Arrays;
import java.util.List;

public enum MisskeyReactionType {

    Favorite("favorite", "like"),
    Renote("reblog", "renote", "retweet", "share"),
    Reply("reply"),
    ;

    private List<String> code;

    MisskeyReactionType(String... codes) {
        this.code = Arrays.asList(codes);
    }

    public List<String> getCode() {
        return code;
    }
}
