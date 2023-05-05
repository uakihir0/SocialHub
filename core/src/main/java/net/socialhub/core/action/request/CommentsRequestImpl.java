package net.socialhub.core.action.request;

import net.socialhub.core.action.RequestActionImpl;
import net.socialhub.core.action.callback.EventCallback;
import net.socialhub.core.model.request.CommentForm;
import net.socialhub.core.define.action.ActionType;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.error.NotSupportedException;
import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Pageable;
import net.socialhub.core.model.service.Paging;
import net.socialhub.core.model.service.Stream;

import java.util.function.Function;

public class CommentsRequestImpl implements CommentsRequest {

    private Function<Paging, Pageable<Comment>> commentsFunction;
    private Function<EventCallback, Stream> streamFunction;
    private RequestActionImpl.SerializeBuilder serializeBuilder;
    private CommentForm commentForm;

    private boolean streamRecommended = true;
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
        return streamRecommended && (streamFunction != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentForm getCommentFrom() {
        if (commentForm == null) {
            commentForm = new CommentForm();
        }
        return commentForm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toSerializedString() {
        return getSerializeBuilder().toJson();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestActionImpl.SerializeBuilder getSerializeBuilder() {
        return serializeBuilder;
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

    public void setSerializeBuilder(RequestActionImpl.SerializeBuilder serializeBuilder) {
        this.serializeBuilder = serializeBuilder;
    }

    public void setCommentForm(CommentForm commentForm) {
        this.commentForm = commentForm;
    }

    public void setStreamRecommended(boolean streamRecommended) {
        this.streamRecommended = streamRecommended;
    }
    //endregion
}
