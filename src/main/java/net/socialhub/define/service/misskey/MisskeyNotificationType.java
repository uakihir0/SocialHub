package net.socialhub.define.service.misskey;

import misskey4j.entity.contant.NotificationType;
import net.socialhub.define.NotificationActionType;

import java.util.stream.Stream;

public enum MisskeyNotificationType {

    FOLLOW(NotificationActionType.FOLLOW, NotificationType.FOLLOW.code()),
    RENOTE(NotificationActionType.SHARE, NotificationType.RENOTE.code()),

    REACTION(null, NotificationType.REACTION.code()),
    MENTION(null, NotificationType.MENTION.code()),
    POLL(null, NotificationType.POLL_VOTE.code()),
    ;

    private NotificationActionType action;
    private String code;

    MisskeyNotificationType(
            NotificationActionType action,
            String code) {
        this.action = action;
        this.code = code;
    }

    public static MisskeyNotificationType of(String code) {
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
