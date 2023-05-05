package net.socialhub.twitter.model;

import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Pageable;
import net.socialhub.core.model.service.Service;
import net.socialhub.core.model.service.Thread;

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
