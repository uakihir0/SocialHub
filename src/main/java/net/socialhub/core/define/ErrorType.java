package net.socialhub.core.define;

import net.socialhub.core.model.error.SocialHubError;

/**
 * Special Error for fur
 * 特殊対応を行う場合のエラーメッセージを追加
 */
public enum ErrorType implements SocialHubError {

    RATE_LIMIT_EXCEEDED(
            "Rate limit exceeded, please try again in a moment.",
            "時間当たりのリクエストの上限に達しました。時間をおいて再度お試しください。"),
    ;

    private final String messageEn;
    private final String messageJa;

    ErrorType(
            String messageEn,
            String messageJa) {
        this.messageEn = messageEn;
        this.messageJa = messageJa;
    }

    public String getMessage(LanguageType languageType) {
        if (languageType == LanguageType.ENGLISH) {
            return messageEn;
        }
        if (languageType == LanguageType.JAPANESE) {
            return messageJa;
        }
        return messageEn;
    }


    @Override
    public String getMessageForUser() {
        return getMessage(LanguageType.ENGLISH);
    }

    @Override
    public String getMessageForUser(LanguageType language) {
        return getMessage(language);
    }
}
