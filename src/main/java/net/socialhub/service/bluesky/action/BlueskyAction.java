package net.socialhub.service.bluesky.action;

import bsky4j.BlueskyException;
import bsky4j.api.entity.atproto.server.ServerCreateSessionRequest;
import bsky4j.api.entity.atproto.server.ServerCreateSessionResponse;
import bsky4j.api.entity.bsky.actor.ActorGetProfileRequest;
import bsky4j.api.entity.bsky.actor.ActorGetProfileResponse;
import bsky4j.api.entity.share.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.socialhub.core.action.AccountActionImpl;
import net.socialhub.core.action.callback.EventCallback;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Channel;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Context;
import net.socialhub.core.model.Error;
import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Notification;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.Relationship;
import net.socialhub.core.model.Service;
import net.socialhub.core.model.Stream;
import net.socialhub.core.model.Thread;
import net.socialhub.core.model.Trend;
import net.socialhub.core.model.User;
import net.socialhub.core.model.error.SocialHubException;
import net.socialhub.core.model.request.CommentForm;
import net.socialhub.core.model.support.ReactionCandidate;
import net.socialhub.logger.Logger;
import net.socialhub.service.microblog.action.MicroBlogAccountAction;

import java.util.Base64;
import java.util.List;
import java.util.Map;

public class BlueskyAction extends AccountActionImpl implements MicroBlogAccountAction {

    private static final Logger logger = Logger.getLogger(BlueskyAction.class);

    private BlueskyAuth auth;
    private String accessJwt;
    private Long expireAt;
    private String did;

    // ============================================================== //
    // Account
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserMe() {
        return proceed(() -> {
            Service service = getAccount().getService();
            Response<ActorGetProfileResponse> response =
                    auth.getAccessor().actor().getProfile(
                            ActorGetProfileRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .actor(getDid())
                                    .build());

            me = BlueskyMapper.user(response.get(), service);
            return me;
        });
    }

    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Response<ActorGetProfileResponse> response =
                    auth.getAccessor().actor().getProfile(
                            ActorGetProfileRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .actor((String) id.getId())
                                    .build());

            return BlueskyMapper.user(response.get(), service);
        });
    }

    /**
     * ハンドルも DID も同じ位置で解釈される
     * https://staging.bsky.app/profile/uakihir0.com
     * https://staging.bsky.app/profile/did:plc:bwdof2anluuf5wmfy2upgulw
     */
    @Override
    public User getUser(String url) {
        return proceed(() -> {
            Service service = getAccount().getService();
            String[] elements = url.split("/");
            String id = elements[elements.length - 1];

            Response<ActorGetProfileResponse> response =
                    auth.getAccessor().actor().getProfile(
                            ActorGetProfileRequest.builder()
                                    .accessJwt(getAccessJwt())
                                    .actor(id)
                                    .build());

            return BlueskyMapper.user(response.get(), service);
        });
    }

    @Override
    public void followUser(Identify id) {
        super.followUser(id);
    }

    @Override
    public void unfollowUser(Identify id) {
        super.unfollowUser(id);
    }

    @Override
    public void muteUser(Identify id) {
        super.muteUser(id);
    }

    @Override
    public void unmuteUser(Identify id) {
        super.unmuteUser(id);
    }

    @Override
    public void blockUser(Identify id) {
        super.blockUser(id);
    }

    @Override
    public void unblockUser(Identify id) {
        super.unblockUser(id);
    }

    @Override
    public Relationship getRelationship(Identify id) {
        return super.getRelationship(id);
    }

    @Override
    public Pageable<User> getFollowingUsers(Identify id, Paging paging) {
        return super.getFollowingUsers(id, paging);
    }

    @Override
    public Pageable<User> getFollowerUsers(Identify id, Paging paging) {
        return super.getFollowerUsers(id, paging);
    }

    @Override
    public Pageable<User> searchUsers(String query, Paging paging) {
        return super.searchUsers(query, paging);
    }

    @Override
    public Pageable<Comment> getHomeTimeLine(Paging paging) {
        return super.getHomeTimeLine(paging);
    }

    @Override
    public Pageable<Comment> getMentionTimeLine(Paging paging) {
        return super.getMentionTimeLine(paging);
    }

    @Override
    public Pageable<Comment> getUserCommentTimeLine(Identify id, Paging paging) {
        return super.getUserCommentTimeLine(id, paging);
    }

    @Override
    public Pageable<Comment> getUserLikeTimeLine(Identify id, Paging paging) {
        return super.getUserLikeTimeLine(id, paging);
    }

    @Override
    public Pageable<Comment> getUserMediaTimeLine(Identify id, Paging paging) {
        return super.getUserMediaTimeLine(id, paging);
    }

    @Override
    public Pageable<Comment> getSearchTimeLine(String query, Paging paging) {
        return super.getSearchTimeLine(query, paging);
    }

    @Override
    public void postComment(CommentForm req) {
        super.postComment(req);
    }

    @Override
    public Comment getComment(Identify id) {
        return super.getComment(id);
    }

    @Override
    public Comment getComment(String url) {
        return super.getComment(url);
    }

    @Override
    public void likeComment(Identify id) {
        super.likeComment(id);
    }

    @Override
    public void unlikeComment(Identify id) {
        super.unlikeComment(id);
    }

    @Override
    public void shareComment(Identify id) {
        super.shareComment(id);
    }

    @Override
    public void unshareComment(Identify id) {
        super.unshareComment(id);
    }

    @Override
    public void reactionComment(Identify id, String reaction) {
        super.reactionComment(id, reaction);
    }

    @Override
    public void unreactionComment(Identify id, String reaction) {
        super.unreactionComment(id, reaction);
    }

    @Override
    public void deleteComment(Identify id) {
        super.deleteComment(id);
    }

    @Override
    public List<ReactionCandidate> getReactionCandidates() {
        return super.getReactionCandidates();
    }

    @Override
    public Context getCommentContext(Identify id) {
        return super.getCommentContext(id);
    }

    @Override
    public Pageable<Channel> getChannels(Identify id, Paging paging) {
        return super.getChannels(id, paging);
    }

    @Override
    public Pageable<Comment> getChannelTimeLine(Identify id, Paging paging) {
        return super.getChannelTimeLine(id, paging);
    }

    @Override
    public Pageable<User> getChannelUsers(Identify id, Paging paging) {
        return super.getChannelUsers(id, paging);
    }

    @Override
    public Pageable<Thread> getMessageThread(Paging paging) {
        return super.getMessageThread(paging);
    }

    @Override
    public Pageable<Comment> getMessageTimeLine(Identify id, Paging paging) {
        return super.getMessageTimeLine(id, paging);
    }

    @Override
    public void postMessage(CommentForm req) {
        super.postMessage(req);
    }

    @Override
    public Stream setHomeTimeLineStream(EventCallback callback) {
        return super.setHomeTimeLineStream(callback);
    }

    @Override
    public Stream setNotificationStream(EventCallback callback) {
        return super.setNotificationStream(callback);
    }

    @Override
    public void favoriteComment(Identify id) {
        super.favoriteComment(id);
    }

    @Override
    public void unfavoriteComment(Identify id) {
        super.unfavoriteComment(id);
    }

    @Override
    public void retweetComment(Identify id) {
        super.retweetComment(id);
    }

    @Override
    public void unretweetComment(Identify id) {
        super.unretweetComment(id);
    }

    @Override
    public Pageable<Channel> getLists(Identify id, Paging paging) {
        return super.getLists(id, paging);
    }

    @Override
    public List<Trend> getTrends(Integer limit) {
        return null;
    }

    @Override
    public Pageable<Notification> getNotification(Paging paging) {
        return null;
    }

    @Override
    public void votePoll(Identify id, List<Integer> choices) {

    }

    @Override
    public Pageable<Comment> getLocalTimeLine(Paging paging) {
        return null;
    }

    @Override
    public Pageable<Comment> getFederationTimeLine(Paging paging) {
        return null;
    }

    @Override
    public Stream setLocalLineStream(EventCallback callback) {
        return null;
    }

    @Override
    public Stream setFederationLineStream(EventCallback callback) {
        return null;
    }

    // ============================================================== //
    // Session
    // ============================================================== //

    private void createSession() {
        Response<ServerCreateSessionResponse> response =
                auth.getAccessor().server().createSession(
                        ServerCreateSessionRequest.builder()
                                .identifier(auth.getIdentifier())
                                .password(auth.getPassword())
                                .build());

        this.did = response.get().getDid();
        this.accessJwt = response.get().getAccessJwt();
        String encoded = this.accessJwt.split("\\.")[1];
        String decoded = new String(Base64.getDecoder().decode(encoded));

        Map<String, Object> json = new Gson().fromJson(decoded,
                new TypeToken<Map<String, Object>>() {
                }.getType());

        Object expire = json.get("exp");
        if (expire instanceof Long) {
            System.out.println("Long");
            this.expireAt = (Long) expire;
        }
        if (expire instanceof Integer) {
            System.out.println("Integer");
            this.expireAt = ((Integer) expire).longValue();
        }
    }

    private String getAccessJwt() {

        // 初回アクセスの場合
        if (accessJwt == null) {
            createSession();
            return accessJwt;
        }

        // 有効期限が切れている場合
        long now = System.currentTimeMillis() / 1000;
        if (now > (expireAt + 30)) {
            createSession();
            return accessJwt;
        }

        return accessJwt;
    }

    private String getDid() {
        if (did == null) {
            createSession();
        }
        return did;
    }

    // ============================================================== //
    // Utils
    // ============================================================== //

    private <T> T proceed(ActionCaller<T, Exception> runner) {
        try {
            return runner.proceed();
        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }


    private static void handleException(Exception e) {
        SocialHubException se = new SocialHubException(e);

        if (e instanceof BlueskyException) {
            BlueskyException be = (BlueskyException) e;
            if (be.getErrorMessage() != null) {
                se.setError(new Error(be.getErrorMessage()));
            }
        }

        throw se;
    }

    // region
    BlueskyAction(Account account, BlueskyAuth auth) {
        this.account(account);
        this.auth(auth);
    }

    BlueskyAction auth(BlueskyAuth auth) {
        this.auth = auth;
        return this;
    }
    // endregion
}
