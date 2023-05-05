package net.socialhub.mastodon.define;

import java.util.stream.Stream;

public enum MastodonVisibility {

    Public("public"),
    Unlisted("unlisted"),
    Private("private"),
    Direct("direct"),
    ;

    private String code;

    MastodonVisibility(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static MastodonVisibility of(String code) {
        return Stream.of(values()) //
                .filter(e -> e.getCode().equals(code)) //
                .findFirst().orElse(null);
    }
}
