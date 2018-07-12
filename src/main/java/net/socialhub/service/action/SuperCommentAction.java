package net.socialhub.service.action;

import net.socialhub.model.service.Comment;

public class SuperCommentAction implements CommentAction {

    private AccountAction action;
    private Comment comment;

    public SuperCommentAction(AccountAction action) {
        this.action = action;
    }

    @Override
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
