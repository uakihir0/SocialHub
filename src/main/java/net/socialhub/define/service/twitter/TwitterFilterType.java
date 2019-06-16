package net.socialhub.define.service.twitter;

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
