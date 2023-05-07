package net.socialhub.core.action;

import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Context;

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
        this.action.reactionComment(comment, action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unreaction(String action) {
        this.action.unreactionComment(comment, action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void like() {
        action.likeComment(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlike() {
        action.unlikeComment(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void share() {
        action.shareComment(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unshare() {
        action.unshareComment(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete() {
        action.deleteComment(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getContext() {
        return action.getCommentContext(comment);
    }
}
