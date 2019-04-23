package net.socialhub.define.service.mastodon;

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
