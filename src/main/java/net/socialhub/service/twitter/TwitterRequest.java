package net.socialhub.service.twitter;

import net.socialhub.model.Account;
import net.socialhub.model.request.CommentForm;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.User;
import net.socialhub.service.action.RequestActionImpl;
import net.socialhub.service.action.request.CommentsRequest;
import net.socialhub.service.action.request.CommentsRequestImpl;

public class TwitterRequest extends RequestActionImpl {

    public TwitterRequest(Account account) {
        super(account);
    }

    // ============================================================== //
    // TimeLine API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getHomeTimeLine() {
        TwitterAction action = (TwitterAction) account.action();
        CommentsRequestImpl request = (CommentsRequestImpl)
                super.getHomeTimeLine();

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
        TwitterAction action = (TwitterAction) account.action();
        CommentsRequestImpl request = (CommentsRequestImpl)
                super.getSearchTimeLine(query);

        request.setStreamFunction((callback) ->
                action.setSearchTimeLineStream(callback, query));

        request.setCommentFormSupplier(() -> {
            CommentForm form = new CommentForm();
            form.message(query + " ");
            return form;
        });
        return request;
    }
}
