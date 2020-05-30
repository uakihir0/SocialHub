package net.socialhub.model.error;

import net.socialhub.define.LanguageType;

public interface SocialHubError {

    /**
     * Get error message with default language
     * デフォルト言語でエラーメッセージを取得
     */
    String getMessageForUser();

    /**
     * Get error message with specified language
     * 言語を指定してエラーメッセージを取得
     */
    String getMessageForUser(LanguageType language);
}
