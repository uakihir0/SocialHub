package net.socialhub.service.action.request;

import net.socialhub.define.action.ActionType;
import net.socialhub.model.Account;
import net.socialhub.model.error.NotSupportedException;
import net.socialhub.model.request.CommentForm;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Stream;
import net.socialhub.service.action.callback.EventCallback;

import java.util.function.Function;
import java.util.function.Supplier;

public class CommentsRequestImpl implements CommentsRequest {

    private Function<Paging, Pageable<Comment>> commentsFunction;
    private Function<EventCallback, Stream> streamFunction;
    private Supplier<CommentForm> commentFormSupplier;
    private ActionType actionType;
    private Account account;

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getComments(Paging paging) {
        if (commentsFunction == null) {
            throw new NotSupportedException();
        }
        return commentsFunction.apply(paging);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream setCommentsStream(EventCallback callback) {
        if (streamFunction == null) {
            throw new NotSupportedException();
        }
        return streamFunction.apply(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canUseCommentsStream() {
        return (streamFunction != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentForm getCommentRequest() {
        if (commentFormSupplier == null) {
            return new CommentForm();
        }
        return commentFormSupplier.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account getAccount() {
        return account;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionType getActionType() {
        return actionType;
    }

    //region // Getter&Setter
    public void setAccount(Account account) {
        this.account = account;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public void setCommentsFunction(Function<Paging, Pageable<Comment>> commentsFunction) {
        this.commentsFunction = commentsFunction;
    }

    public void setStreamFunction(Function<EventCallback, Stream> streamFunction) {
        this.streamFunction = streamFunction;
    }

    public void setCommentFormSupplier(Supplier<CommentForm> commentFormSupplier) {
        this.commentFormSupplier = commentFormSupplier;
    }
    //endregion
}
