package net.socialhub.core.define;

/**
 * Notification Action Types
 */
public enum NotificationActionType {

    MENTION("mention"),
    FOLLOW("follow"),
    FOLLOW_REQUEST("follow_request"),
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
