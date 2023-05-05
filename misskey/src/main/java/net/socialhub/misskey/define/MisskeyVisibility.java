package net.socialhub.misskey.define;

import java.util.stream.Stream;

public enum MisskeyVisibility {

    Public("public"),
    Home("home"),
    Followers("followers"),
    Specified("specified"),
    Message("message"),
    ;

    private String code;

    MisskeyVisibility(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static MisskeyVisibility of(String code) {
        return Stream.of(values()) //
                .filter(e -> e.getCode().equals(code)) //
                .findFirst().orElse(null);
    }
}
