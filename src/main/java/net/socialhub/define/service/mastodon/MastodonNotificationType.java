package net.socialhub.define.service.mastodon;

import net.socialhub.define.NotificationType;

import java.util.stream.Stream;

public enum MastodonNotificationType {

    MENTION(NotificationType.MENTION, "mention"),
    FOLLOW(NotificationType.FOLLOW, "follow"),
    REBLOG(NotificationType.SHARE, "reblog"),
    FAVOURITE(NotificationType.LIKE, "favourite"),

    STATUS(null, "status"),
    POLL(null, "poll"),

    ;

    private NotificationType type;
    private String code;

    MastodonNotificationType(
            NotificationType type,
            String code) {
        this.type = type;
        this.code = code;
    }

    public static MastodonNotificationType of(String code) {
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

