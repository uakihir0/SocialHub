package net.socialhub.service.misskey.action;

import com.google.gson.Gson;
import net.socialhub.core.action.RequestActionImpl;
import net.socialhub.core.action.request.CommentsRequest;
import net.socialhub.core.action.request.CommentsRequestImpl;
import net.socialhub.core.define.action.TimeLineActionType;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Request;
import net.socialhub.core.model.User;
import net.socialhub.logger.Logger;
import net.socialhub.service.microblog.action.MicroBlogRequestAction;
import net.socialhub.service.microblog.define.MicroBlogActionType;

import static net.socialhub.service.microblog.define.MicroBlogActionType.FeaturedTimeline;
import static net.socialhub.service.microblog.define.MicroBlogActionType.FederationTimeLine;
import static net.socialhub.service.microblog.define.MicroBlogActionType.LocalTimeLine;

public class MisskeyRequest extends RequestActionImpl implements MicroBlogRequestAction {

    private Logger log = Logger.getLogger(MisskeyRequest.class);

    public MisskeyRequest(Account account) {
        super(account);
    }

    // ============================================================== //
    // Mastodon TimeLine API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getLocalTimeLine() {
        MisskeyAction action = (MisskeyAction) account.action();
        CommentsRequestImpl request = getCommentsRequest(
                LocalTimeLine, action::getLocalTimeLine,
                new SerializeBuilder(LocalTimeLine));

        request.setStreamFunction(action::setLocalLineStream);
        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getFederationTimeLine() {
        MisskeyAction action = (MisskeyAction) account.action();
        CommentsRequestImpl request = getCommentsRequest(
                FederationTimeLine, action::getFederationTimeLine,
                new SerializeBuilder(FederationTimeLine));

        request.setStreamFunction(action::setFederationLineStream);
        return request;
    }


    /**
     * Get Featured Timeline
     * (No Streaming)
     */
    public CommentsRequest getFeaturedTimeLine() {
        MisskeyAction action = (MisskeyAction) account.action();
        return getCommentsRequest(
                FeaturedTimeline, action::getFeaturedTimeLine,
                new SerializeBuilder(FeaturedTimeline));
    }

    // ============================================================== //
    // TimeLine API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getHomeTimeLine() {
        MisskeyAction action = (MisskeyAction) account.action();
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
        CommentsRequestImpl request = (CommentsRequestImpl)
                super.getSearchTimeLine(query);
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

            // Misskey
            if (isTypeIncluded(MicroBlogActionType.values(), action)) {
                switch (MicroBlogActionType.valueOf(action)) {

                    case LocalTimeLine:
                        return getLocalTimeLine();
                    case FederationTimeLine:
                        return getFederationTimeLine();
                    case FeaturedTimeline:
                        return getFeaturedTimeLine();
                    default:
                        log.debug("invalid misskey action type: " + action);
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