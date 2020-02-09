package net.socialhub.model.request;

import java.util.ArrayList;
import java.util.Collections;
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

    /** Copy this object */
    public CommentForm copy() {
        CommentForm form = new CommentForm();
        form.text(text);
        form.targetId(targetId);
        form.sensitive(isSensitive);
        form.message(isMessage);

        if (images != null) {
            for (MediaForm image : images) {
                form.addImage(image.copy());
            }
        }
        if (params != null) {
            for (String key : params.keySet()) {
                form.param(key, params.get(key));
            }
        }
        return form;
    }

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
        MediaForm req = new MediaForm();
        req.setData(image);
        req.setName(name);
        return addImage(req);
    }

    /**
     * Add One Image
     */
    public CommentForm addImage(MediaForm req) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }

        this.images.add(req);
        return this;
    }

    /**
     * s
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
        if (params == null) {
            return Collections.emptyMap();
        }
        return params;
    }

    public boolean isMessage() {
        return isMessage;
    }
    //endregion
}
