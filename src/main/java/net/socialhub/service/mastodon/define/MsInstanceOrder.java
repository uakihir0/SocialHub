package net.socialhub.service.mastodon.define;

public enum MsInstanceOrder {

    ASC("asc"),
    DESC("desc");

    private String code;

    MsInstanceOrder(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
