package net.socialhub.model.service;

import net.socialhub.define.MediaTypeEnum;

import java.io.Serializable;

/**
 * Media Model
 * メディアモデル
 */
public class Media implements Serializable {

    /** Type of this media */
    private MediaTypeEnum type;

    /** Link of source media url */
    private String sourceUrl;

    /** Link of preview image url */
    private String previewUrl;

    //region // Getter&Setter
    public MediaTypeEnum getType() {
        return type;
    }

    public void setType(MediaTypeEnum type) {
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
