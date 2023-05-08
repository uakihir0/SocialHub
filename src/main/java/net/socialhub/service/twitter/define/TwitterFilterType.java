package net.socialhub.service.twitter.define;

public enum TwitterFilterType {

    Images("images"),
    Videos("videos"),
    Links("links"),
    Verified("verified"),
    Retweets("retweets"),
    Media("media"),

    ;

    private String code;

    TwitterFilterType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
