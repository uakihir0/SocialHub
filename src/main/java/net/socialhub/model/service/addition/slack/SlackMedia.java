package net.socialhub.model.service.addition.slack;

import net.socialhub.model.service.Media;

/**
 * Media in Slack
 * Slack のメディア情報
 */
public class SlackMedia extends Media {

    /** AccessToken to get file */
    private String accessToken;

    //region // Getter&Setter
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    //endregion
}
