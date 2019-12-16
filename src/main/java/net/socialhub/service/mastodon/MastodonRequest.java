package net.socialhub.service.mastodon;

import com.google.gson.Gson;
import net.socialhub.define.action.TimeLineActionType;
import net.socialhub.define.action.service.MastodonActionType;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.error.NotSupportedException;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Request;
import net.socialhub.model.service.User;
import net.socialhub.service.action.RequestActionImpl;
import net.socialhub.service.action.request.CommentsRequest;
import net.socialhub.service.action.request.CommentsRequestImpl;

import static net.socialhub.define.action.service.MastodonActionType.FederationTimeLine;
import static net.socialhub.define.action.service.MastodonActionType.LocalTimeLine;

public class MastodonRequest extends RequestActionImpl {

    private Logger log = Logger.getLogger(MastodonRequest.class);

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
                new SerializeBuilder(LocalTimeLine));

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
                new SerializeBuilder(FederationTimeLine));

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
        throw new NotSupportedException();
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

            // Mastodon
            if (isTypeIncluded(MastodonActionType.values(), action)) {
                switch (MastodonActionType.valueOf(action)) {

                case LocalTimeLine:
                    return getLocalTimeLine();
                case FederationTimeLine:
                    return getFederationTimeLine();
                default:
                    log.debug("invalid mastodon action type: " + action);
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
