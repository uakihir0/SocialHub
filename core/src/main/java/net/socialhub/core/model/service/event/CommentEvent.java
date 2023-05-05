package net.socialhub.core.model.service.event;

import net.socialhub.core.model.service.Comment;

public class CommentEvent {

    private Comment comment;

    public CommentEvent(Comment comment) {
        this.comment = comment;
    }

    //region // Getter&Setter
    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
    //endregion
}
