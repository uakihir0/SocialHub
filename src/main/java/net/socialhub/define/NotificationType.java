package net.socialhub.define;

/**
 * Notification Types
 */
public enum NotificationType {

    FOLLOW("follow"),
    SHARE("share"),
    LIKE("like");

    private String code;

    NotificationType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
