package net.socialhub.service.twitter.model;

import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Service;
import net.socialhub.core.model.Thread;

public class TwitterThread extends Thread {
    public TwitterThread(Service service) {
        super(service);
    }

    /**
     * List of Comments
     * コメント一覧
     */
    private Pageable<Comment> comments;

    //region // Getter&Setter
    public Pageable<Comment> getComments() {
        return comments;
    }

    public void setComments(Pageable<Comment> comments) {
        this.comments = comments;
    }
    //endregion
}
