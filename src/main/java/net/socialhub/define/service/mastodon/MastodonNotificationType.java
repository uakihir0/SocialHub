package net.socialhub.define.service.mastodon;

public enum MastodonNotificationType {

    follow("follow"),
    favourite("favourite"),
    reblog("reblog"),
    mention("mention");

    private String code;

    MastodonNotificationType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
