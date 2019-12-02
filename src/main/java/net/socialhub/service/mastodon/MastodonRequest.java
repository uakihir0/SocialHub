package net.socialhub.service.mastodon;

import net.socialhub.model.Account;
import net.socialhub.model.error.NotSupportedException;
import net.socialhub.model.request.CommentForm;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.User;
import net.socialhub.service.action.RequestActionImpl;
import net.socialhub.service.action.request.CommentsRequest;
import net.socialhub.service.action.request.CommentsRequestImpl;

import static net.socialhub.define.action.service.MastodonActionType.FederationTimeLine;
import static net.socialhub.define.action.service.MastodonActionType.LocalTimeLine;

public class MastodonRequest extends RequestActionImpl {

    public MastodonRequest(Account account) {
        super(account);
    }

    // ============================================================== //
    // Mastodon TimeLine API
    // ============================================================== //

    /**
     * Get Local TimeLine
     */
    public CommentsRequest getLocalTimeLine() {
        MastodonAction action = (MastodonAction) account.action();
        CommentsRequestImpl request = getCommentsRequest(
                LocalTimeLine, action::getLocalTimeLine,
                () -> new SerializeBuilder(LocalTimeLine).toJson());

        request.setStreamFunction(action::setLocalLineStream);
        return request;
    }

    /**
     * Get Federation TimeLine
     */
    public CommentsRequest getFederationTimeLine() {
        MastodonAction action = (MastodonAction) account.action();
        CommentsRequestImpl request = getCommentsRequest(
                FederationTimeLine, action::getFederationTimeLine,
                () -> new SerializeBuilder(FederationTimeLine).toJson());

        request.setStreamFunction(action::setFederationLineStream);
        return request;
    }

    // ============================================================== //
    // TimeLine API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getHomeTimeLine() {
        MastodonAction action = (MastodonAction) account.action();
        CommentsRequestImpl request = (CommentsRequestImpl) super.getHomeTimeLine();

        request.setStreamFunction(action::setHomeTimeLineStream);
        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getUserCommentTimeLine(Identify id) {
        CommentsRequestImpl request = (CommentsRequestImpl)
                super.getUserCommentTimeLine(id);

        if (id instanceof User) {
            User user = (User) id;
            request.setCommentFormSupplier(() -> {
                CommentForm form = new CommentForm();
                form.message(user.getAccountIdentify() + " ");
                return form;
            });
        }
        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getUserLikeTimeLine(Identify id) {
        CommentsRequestImpl request = (CommentsRequestImpl)
                super.getUserLikeTimeLine(id);

        if (id instanceof User) {
            User user = (User) id;
            request.setCommentFormSupplier(() -> {
                CommentForm form = new CommentForm();
                form.message(user.getAccountIdentify() + " ");
                return form;
            });
        }
        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getUserMediaTimeLine(Identify id) {
        CommentsRequestImpl request = (CommentsRequestImpl)
                super.getUserMediaTimeLine(id);

        if (id instanceof User) {
            User user = (User) id;
            request.setCommentFormSupplier(() -> {
                CommentForm form = new CommentForm();
                form.message(user.getAccountIdentify() + " ");
                return form;
            });
        }
        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getSearchTimeLine(String query) {
        throw new NotSupportedException();
    }
}
