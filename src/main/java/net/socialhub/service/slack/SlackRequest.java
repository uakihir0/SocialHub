package net.socialhub.service.slack;

import net.socialhub.define.service.slack.SlackFormKey;
import net.socialhub.model.Account;
import net.socialhub.model.service.Identify;
import net.socialhub.service.action.RequestActionImpl;
import net.socialhub.service.action.request.CommentsRequest;

import static net.socialhub.define.action.TimeLineActionType.MessageTimeLine;

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
    public CommentsRequest getMessageTimeLine(Identify id) {
        CommentsRequest request = getCommentsRequest(MessageTimeLine,
                (paging) -> account.action().getMessageTimeLine(id, paging),
                new SerializeBuilder(MessageTimeLine)
                        .add("id", id.getSerializedIdString()));

        request.getCommentFrom()
                .param(SlackFormKey.CHANNEL_KEY, id.getId())
                .message(true);
        return request;
    }
}
