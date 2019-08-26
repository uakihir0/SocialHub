package net.socialhub.model.service.event;

import net.socialhub.model.service.Comment;

public class UpdateCommentEvent {

    private Comment comment;

    public UpdateCommentEvent(Comment comment) {
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
