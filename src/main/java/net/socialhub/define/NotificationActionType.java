package net.socialhub.define;

/**
 * Notification Action Types
 */
public enum NotificationActionType {

    MENTION("mention"),
    FOLLOW("follow"),
    SHARE("share"),
    LIKE("like"),
    ;

    private String code;

    NotificationActionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
