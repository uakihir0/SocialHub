package net.socialhub.mastodon.define;

public enum MastodonImageSize {

    Small("small"),

    Original("original");

    private String code;

    MastodonImageSize(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
