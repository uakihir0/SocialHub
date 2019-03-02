package net.socialhub.define.service;

public enum MsInstanceOrderEnum {

    ASC("asc"),
    DESC("desc");

    private String code;

    MsInstanceOrderEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
