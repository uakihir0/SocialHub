package net.socialhub.model.request;

import java.util.List;

public class CommentRequest {

    private String message;

    private List<byte[]> images;

    public CommentRequest message(String message){
        this.message = message;
        return this;
    }
}
