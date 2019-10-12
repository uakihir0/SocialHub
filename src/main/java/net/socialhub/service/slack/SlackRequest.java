package net.socialhub.service.slack;

import net.socialhub.model.Account;
import net.socialhub.model.request.CommentForm;
import net.socialhub.service.action.RequestActionImpl;
import net.socialhub.service.action.request.CommentsRequest;
import net.socialhub.service.action.request.CommentsRequestImpl;

public class SlackRequest extends RequestActionImpl {

    public SlackRequest(Account account) {
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
        SlackAction action = (SlackAction) account.action();
        CommentsRequestImpl request = (CommentsRequestImpl)
                super.getHomeTimeLine();

        request.setCommentFormSupplier(() -> {
            CommentForm form = new CommentForm();
            form.param("channel", action.getGeneralChannel());
            return form;
        });
        return request;
    }
}
