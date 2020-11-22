package net.socialhub.define.service.mastodon;

import net.socialhub.define.NotificationActionType;

import java.util.stream.Stream;

public enum MastodonNotificationType {

    MENTION(NotificationActionType.MENTION, "mention"),
    FOLLOW(NotificationActionType.FOLLOW, "follow"),
    REBLOG(NotificationActionType.SHARE, "reblog"),
    FAVOURITE(NotificationActionType.LIKE, "favourite"),

    STATUS(null, "status"),
    POLL(null, "poll"),
    ;

    private NotificationActionType action;
    private String code;

    MastodonNotificationType(
            NotificationActionType action,
            String code) {
        this.action = action;
        this.code = code;
    }

    public static MastodonNotificationType of(String code) {
        return Stream.of(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst().orElse(null);
    }

    // region // Getter
    public NotificationActionType getAction() {
        return action;
    }

    public String getCode() {
        return code;
    }
    // endregion
}

