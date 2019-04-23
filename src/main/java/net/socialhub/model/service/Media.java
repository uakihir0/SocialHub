package net.socialhub.model.service;

import net.socialhub.define.MediaType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Media Model
 * メディアモデル
 */
public class Media implements Serializable {

    /** Type of this media */
    private MediaType type;

    /** Link of source media url */
    private String sourceUrl;

    /** Link of preview image url */
    private String previewUrl;

    /** Get request header for authorize */
    public Map<String, String> getRequestHeader() {
        return new HashMap<>();
    }

    //region // Getter&Setter
    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
    //endregion
}
