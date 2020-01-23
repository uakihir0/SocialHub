package net.socialhub.model.service.addition.twitter;

import net.socialhub.model.service.Media;

import java.util.HashMap;
import java.util.Map;

/**
 * Media in Twitter
 * Twitter のメディア情報
 */
public class TwitterMedia extends Media {

    private String mp4VideoUrl;

    private String streamVideoUrl;

    private String authorizationHeader;

    @Override
    public Map<String, String> getRequestHeader() {
        Map<String, String> model = new HashMap<>();
        model.put("Authorization", authorizationHeader);
        return model;
    }

    //region // Getter&Setter
    public String getMp4VideoUrl() {
        return mp4VideoUrl;
    }

    public void setMp4VideoUrl(String mp4VideoUrl) {
        this.mp4VideoUrl = mp4VideoUrl;
    }

    public String getStreamVideoUrl() {
        return streamVideoUrl;
    }

    public void setStreamVideoUrl(String streamVideoUrl) {
        this.streamVideoUrl = streamVideoUrl;
    }

    public String getAuthorizationHeader() {
        return authorizationHeader;
    }

    public void setAuthorizationHeader(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }
    //endregion
}
