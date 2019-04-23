package net.socialhub.define.service.mastodon;

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
