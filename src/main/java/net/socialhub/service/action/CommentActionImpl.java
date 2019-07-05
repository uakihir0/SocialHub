package net.socialhub.service.action;

import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Context;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public Comment refresh() {
        return action.getComment(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reaction(String action) {
        this.action.reaction(comment, action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unreaction(String action) {
        this.action.unreaction(comment, action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void like() {
        action.like(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlike() {
        action.unlike(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void share() {
        action.share(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unshare() {
        action.unshare(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getCommentContext() {
        return action.getCommentContext(comment);
    }
}
