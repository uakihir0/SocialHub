package net.socialhub.define.service;

public enum MsInstanceSortEnum {

    NAME("name"),
    UPTIME("uptime"),
    HTTPS_SCORE("https_score"),
    OBS_SCORE("obs_score"),
    USERS("users"),
    STATUSES("statuses"),
    CONNECTIONS("connections"),
    ACTIVE_USERS("active_users");

    private String code;

    MsInstanceSortEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
