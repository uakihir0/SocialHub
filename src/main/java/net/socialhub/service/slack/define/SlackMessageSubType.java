package net.socialhub.service.slack.define;

import java.util.stream.Stream;

public enum SlackMessageSubType {

    BotMessage("bot_message");

    private String code;

    SlackMessageSubType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static SlackMessageSubType of(String code) {
        return Stream.of(values()) //
                .filter(e -> e.getCode().equals(code)) //
                .findFirst().orElse(null);
    }
}
