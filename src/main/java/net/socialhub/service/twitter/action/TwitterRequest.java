package net.socialhub.service.twitter.action;

import com.google.gson.Gson;
import net.socialhub.core.action.RequestActionImpl;
import net.socialhub.core.action.request.CommentsRequest;
import net.socialhub.core.action.request.CommentsRequestImpl;
import net.socialhub.core.action.request.UsersRequest;
import net.socialhub.core.define.action.TimeLineActionType;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Request;
import net.socialhub.core.model.User;
import net.socialhub.logger.Logger;
import net.socialhub.service.twitter.define.TwitterActionType;

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
    // Users API
    // ============================================================== //

    /**
     * Get Users who favorites specified tweet
     * 特定のツイートをいいねしたユーザーを取得
     */
    public UsersRequest getUsersFavoriteBy(Identify id) {
        return getUsersRequest(TwitterActionType.FavoriteByUsers,
                (paging) -> ((TwitterAction) account.action()).getUsersFavoriteBy(id, paging),
                new RequestActionImpl.SerializeBuilder(TwitterActionType.FavoriteByUsers)
                        .add("id", id.getSerializedIdString()));
    }

    /**
     * Get Users who retweeted specified tweet
     * 特定のツイートをリツイートしたユーザーを取得
     */
    public UsersRequest getUsersRetweetBy(Identify id) {
        return getUsersRequest(TwitterActionType.RetweetByUsers,
                (paging) -> ((TwitterAction) account.action()).getUsersRetweetBy(id, paging),
                new RequestActionImpl.SerializeBuilder(TwitterActionType.RetweetByUsers)
                        .add("id", id.getSerializedIdString()));
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

            // Identify
            Identify id = null;
            if (params.contains("id")) {
                id = new Identify(account.getService());
                id.setSerializedIdString(params.get("id"));
            }

            // Twitter
            if (isTypeIncluded(TwitterActionType.values(), action)) {
                switch (TwitterActionType.valueOf(action)) {

                    case FavoriteByUsers:
                        return getUsersFavoriteBy(id);
                    case RetweetByUsers:
                        return getUsersRetweetBy(id);
                    default:
                        log.debug("invalid twitter action type: " + action);
                        return null;
                }
            }

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
