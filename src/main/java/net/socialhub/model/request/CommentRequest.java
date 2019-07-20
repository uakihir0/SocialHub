package net.socialhub.model.request;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    /** Image Handlers */
    private List<Consumer<MediaRequest>> imageHandlers;

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
     * Add Image Handler
     */
    public CommentRequest addImageHandler(Consumer<MediaRequest> imageHandler) {
        if (this.imageHandlers == null) {
            this.imageHandlers = new ArrayList<>();
        }
        this.imageHandlers.add(imageHandler);
        return this;
    }

    /**
     * Add One Image
     */
    public CommentRequest addImage(byte[] image) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }

        MediaRequest media = new MediaRequest();
        media.setData(image);

        if (imageHandlers != null) {
            imageHandlers.forEach((handler) -> //
                    handler.accept(media));
        }

        this.images.add(media);
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

    public List<Consumer<MediaRequest>> getImageHandlers() {
        return imageHandlers;
    }
    //endregion
}
