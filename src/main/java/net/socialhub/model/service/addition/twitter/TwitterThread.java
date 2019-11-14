package net.socialhub.model.service.addition.twitter;

import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.Thread;

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
