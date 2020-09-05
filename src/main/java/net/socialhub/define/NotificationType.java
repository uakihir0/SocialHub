package net.socialhub.define;

/**
 * Notification Types
 */
public enum NotificationType {

    MENTION("mention"),
    FOLLOW("follow"),
    SHARE("share"),
    LIKE("like"),
    ;

    private String code;

    NotificationType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
