package net.socialhub.service.action;

import net.socialhub.model.service.Comment;

public class CommentActionImpl implements CommentAction {

    private AccountAction action;
    private Comment comment;

    public CommentActionImpl(AccountAction action) {
        this.action = action;
    }

    public CommentAction comment(Comment comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public void like() {
        action.like(comment);
    }

    @Override
    public void unlike() {
        action.unlike(comment);
    }
}
