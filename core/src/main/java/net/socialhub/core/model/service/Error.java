package net.socialhub.core.model.service;

import net.socialhub.core.define.LanguageType;
import net.socialhub.core.model.error.SocialHubError;

import java.util.HashMap;
import java.util.Map;

public class Error implements SocialHubError {

    /**
     * Default message.
     * デフォルトメッセージ
     */
    private final String defaultMessage;

    /**
     * Error message map.
     * エラーメッセージ
     */
    private final Map<LanguageType, String> messages = new HashMap<>();


    public Error(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    /**
     * Set error message for specified language.
     * 特定の言語でのエラーメッセージを取得
     */
    public void addMessage(LanguageType language, String message) {
        messages.put(language, message);
    }

    @Override
    public String getMessageForUser() {
        return defaultMessage;
    }

    @Override
    public String getMessageForUser(LanguageType language) {
        if (messages.containsKey(language)) {
            messages.get(language);
        }
        return defaultMessage;
    }
}
