package net.socialhub.service.twitter;

import com.google.gson.Gson;
import net.socialhub.define.action.TimeLineActionType;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Request;
import net.socialhub.model.service.User;
import net.socialhub.service.action.RequestActionImpl;
import net.socialhub.service.action.request.CommentsRequest;
import net.socialhub.service.action.request.CommentsRequestImpl;

public class TwitterRequest extends RequestActionImpl {

    private Logger log = Logger.getLogger(TwitterRequest.class);

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
        request.setStreamRecommended(false);
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
            setCommentIdentify(request, ((User) id).getAccountIdentify());
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
            setCommentIdentify(request, ((User) id).getAccountIdentify());
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
            setCommentIdentify(request, ((User) id).getAccountIdentify());
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
        request.getCommentFrom().text(query + " ");
        return request;
    }

    // ============================================================== //
    // From Serialized
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Request fromSerializedString(String serialize) {

        try {
            SerializeParams params = new Gson().fromJson(serialize, SerializeParams.class);
            String action = params.get("action");

            Request result = super.fromSerializedString(serialize);

            // Comment Mentions
            if (result instanceof CommentsRequest) {
                CommentsRequest req = (CommentsRequest) result;
                switch (TimeLineActionType.valueOf(action)) {

                case UserCommentTimeLine:
                case UserLikeTimeLine:
                case UserMediaTimeLine:
                    setCommentIdentify(req, params.get("to"));
                    break;
                default:
                    break;
                }
            }

            return result;

        } catch (Exception e) {
            log.debug("json parse error (mastodon).", e);
            return null;
        }
    }

    // ============================================================== //
    // Support
    // ============================================================== //

    private void setCommentIdentify(CommentsRequest request, String identify) {
        request.getSerializeBuilder().add("to", identify);
        request.getCommentFrom().text(identify + " ");
    }
}
