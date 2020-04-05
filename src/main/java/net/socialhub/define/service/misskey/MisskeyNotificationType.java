package net.socialhub.define.service.misskey;

import net.socialhub.define.NotificationType;

import java.util.stream.Stream;

public enum MisskeyNotificationType {

    FOLLOW(NotificationType.FOLLOW,
            misskey4j.entity.contant.NotificationType.FOLLOW.code()),
    RENOTE(NotificationType.SHARE,
            misskey4j.entity.contant.NotificationType.REMOTE.code()),

    REACTION(null, misskey4j.entity.contant.NotificationType.REACTION.code()),
    MENTION(null, misskey4j.entity.contant.NotificationType.MENTION.code()),
    POLL(null, misskey4j.entity.contant.NotificationType.POLL_VOTE.code()),
    ;

    private NotificationType type;
    private String code;

    MisskeyNotificationType(
            NotificationType type,
            String code) {
        this.type = type;
        this.code = code;
    }

    public static MisskeyNotificationType of(String code) {
        return Stream.of(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst().orElse(null);
    }

    // region // Getter
    public NotificationType getType() {
        return type;
    }

    public String getCode() {
        return code;
    }
    // endregion
}
