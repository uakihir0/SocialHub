package net.socialhub.service.bluesky.define;

import net.socialhub.core.define.NotificationActionType;

import java.util.stream.Stream;

/**
 * like, repost, follow, mention, reply, quote
 */
public enum BlueskyNotificationType {

    MENTION(NotificationActionType.MENTION, "mention"),
    REPLY(NotificationActionType.MENTION, "replay"),
    FOLLOW(NotificationActionType.FOLLOW, "follow"),
    REPOST(NotificationActionType.SHARE, "repost"),
    LIKE(NotificationActionType.LIKE, "like"),
    ;

    private final NotificationActionType action;
    private final String code;

    BlueskyNotificationType(
            NotificationActionType action,
            String code) {
        this.action = action;
        this.code = code;
    }

    public static BlueskyNotificationType of(String code) {
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
