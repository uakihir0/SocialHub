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
    private String message;

    /** Reply or Thread ID */
    private Object replyId;

    /** Images */
    private List<MediaForm> images;

    /** Sensitive */
    private Boolean isSensitive;

    /** Other params */
    private Map<String, Object> params;

    // ============================================================== //
    // Functions
    // ============================================================== //

    /**
     * Set Messages
     */
    public CommentForm message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Set Reply ID
     */
    public CommentForm replyId(Object replyId) {
        this.replyId = replyId;
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
    public String getMessage() {
        return message;
    }

    public Object getReplyId() {
        return replyId;
    }

    public List<MediaForm> getImages() {
        return images;
    }

    public Boolean getSensitive() {
        return isSensitive;
    }

    public Map<String, Object> getParams() {
        return params;
    }
    //endregion
}
