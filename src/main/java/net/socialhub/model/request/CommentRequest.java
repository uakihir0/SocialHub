package net.socialhub.model.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentRequest {

    // ============================================================== //
    // Fields
    // ============================================================== //

    /** Text */
    private String message;

    /** Reply or Thread ID */
    private Object replyId;

    /** Images */
    private List<MediaRequest> images;

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
    public CommentRequest message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Set Reply ID
     */
    public CommentRequest replyId(Object replyId) {
        this.replyId = replyId;
        return this;
    }

    /**
     * Add One Image
     */
    public CommentRequest addImage(byte[] image, String name) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }

        MediaRequest req = new MediaRequest();
        req.setData(image);
        req.setName(name);
        this.images.add(req);

        return this;
    }

    /**
     * Remove One Image
     */
    public CommentRequest removeImage(int index) {
        this.images.remove(index);
        return this;
    }

    /**
     * Set Sensitive
     */
    public CommentRequest sensitive(boolean isSensitive) {
        this.isSensitive = isSensitive;
        return this;
    }

    /**
     * Set addition params
     */
    public CommentRequest param(String key, Object value) {
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

    public List<MediaRequest> getImages() {
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
