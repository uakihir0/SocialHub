package net.socialhub.service.slack.model;

import net.socialhub.core.model.Media;

import java.util.HashMap;
import java.util.Map;

/**
 * Slack Media Model
 * Slack のメディア情報
 */
public class SlackMedia extends Media {

    /** AccessToken to get file */
    private String accessToken;

    @Override
    public Map<String, String> getRequestHeader() {
        Map<String, String> model = new HashMap<>();
        model.put("Authorization", "Bearer " + accessToken);
        return model;
    }

    //region // Getter&Setter
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    //endregion
}
