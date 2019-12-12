package net.socialhub.model.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentForm {

    // ============================================================== //
    // Fields
    // ============================================================== //

    /** Text */
    private String text;

    /** Reply or Thread ID */
    private Object targetId;

    /** Images */
    private List<MediaForm> images;

    /** Is Sensitive Content? */
    private boolean isSensitive = false;

    /** Is Message? */
    private boolean isMessage = false;

    /** Other params */
    private Map<String, Object> params;

    // ============================================================== //
    // Functions
    // ============================================================== //

    /**
     * Set Text
     */
    public CommentForm text(String text) {
        this.text = text;
        return this;
    }

    /**
     * Set Reply (Thread) ID
     */
    public CommentForm targetId(Object targetId) {
        this.targetId = targetId;
        return this;
    }

    /**
     * Add One Image
     */
    public CommentForm addImage(byte[] image, String name) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }

        MediaForm req = new MediaForm();
        req.setData(image);
        req.setName(name);
        this.images.add(req);

        return this;
    }

    /**
     * Remove One Image
     */
    public CommentForm removeImage(int index) {
        this.images.remove(index);
        return this;
    }

    /**
     * Set Sensitive
     */
    public CommentForm sensitive(boolean isSensitive) {
        this.isSensitive = isSensitive;
        return this;
    }

    /**
     * Set Message
     */
    public CommentForm message(boolean isMessage) {
        this.isMessage = isMessage;
        return this;
    }

    /**
     * Set addition params
     */
    public CommentForm param(String key, Object value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(key, value);
        return this;
    }

    // ============================================================== //
    // Getters
    // ============================================================== //

    //region // Getter&Setter
    public String getText() {
        return text;
    }

    public Object getTargetId() {
        return targetId;
    }

    public List<MediaForm> getImages() {
        return images;
    }

    public boolean isSensitive() {
        return isSensitive;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public boolean isMessage() {
        return isMessage;
    }
    //endregion
}
