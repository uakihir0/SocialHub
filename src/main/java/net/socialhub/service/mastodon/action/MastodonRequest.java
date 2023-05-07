package net.socialhub.service.mastodon.action;

import com.google.gson.Gson;
import net.socialhub.core.define.action.TimeLineActionType;
import net.socialhub.service.microblog.define.MicroBlogActionType;
import net.socialhub.logger.Logger;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Request;
import net.socialhub.core.model.User;
import net.socialhub.core.action.RequestActionImpl;
import net.socialhub.core.action.request.CommentsRequest;
import net.socialhub.core.action.request.CommentsRequestImpl;
import net.socialhub.service.microblog.action.MicroBlogRequestAction;

import java.util.Comparator;

import static net.socialhub.core.define.action.TimeLineActionType.MessageTimeLine;

public class MastodonRequest extends RequestActionImpl implements MicroBlogRequestAction {

    private final Logger log = Logger.getLogger(MastodonRequest.class);

    public MastodonRequest(Account account) {
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
        MastodonAction action = (MastodonAction) account.action();
        CommentsRequestImpl request = getCommentsRequest(
                MicroBlogActionType.LocalTimeLine, action::getLocalTimeLine,
                new SerializeBuilder(MicroBlogActionType.LocalTimeLine));

        request.setStreamFunction(action::setLocalLineStream);
        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getFederationTimeLine() {
        MastodonAction action = (MastodonAction) account.action();
        CommentsRequestImpl request = getCommentsRequest(
                MicroBlogActionType.FederationTimeLine, action::getFederationTimeLine,
                new SerializeBuilder(MicroBlogActionType.FederationTimeLine));

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
        CommentsRequestImpl request = (CommentsRequestImpl)
                super.getSearchTimeLine(query);
        request.getCommentFrom().text(query + " ");
        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentsRequest getMessageTimeLine(Identify id) {
        CommentsRequestImpl request = new CommentsRequestImpl();
        request.setActionType(MessageTimeLine);
        request.setAccount(account);
        request.getCommentFrom()
                .message(true);

        request.setCommentsFunction((paging) -> {
            Pageable<Comment> comments = account
                    .action().getMessageTimeLine(id, paging);

            // 最新の投稿の ID を取得してコメント対象に設定
            Long maxId = (Long) comments.getEntities().stream()
                    .max(Comparator.comparing(Comment::getCreateAt))
                    .map(Identify::getId).orElse(null);
            request.getCommentFrom().replyId(maxId);
            return comments;
        });

        request.setSerializeBuilder(
                new SerializeBuilder(MessageTimeLine)
                        .add("id", id.getSerializedIdString()));
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

            // Mastodon
            if (isTypeIncluded(MicroBlogActionType.values(), action)) {
                switch (MicroBlogActionType.valueOf(action)) {

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
