package net.socialhub.service.twitter;

import net.socialhub.define.MediaType;
import net.socialhub.define.service.twitter.TwitterReactionType;
import net.socialhub.model.Account;
import net.socialhub.model.error.NotSupportedException;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Relationship;
import net.socialhub.model.service.User;
import net.socialhub.model.service.*;
import net.socialhub.model.service.paging.CursorPaging;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;
import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

import static net.socialhub.define.action.OtherActionType.*;
import static net.socialhub.define.action.TimeLineActionType.*;

/**
 * Twitter Actions
 * (All Actions)
 */
public class TwitterAction extends AccountActionImpl {

    private ServiceAuth<Twitter> auth;

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
            twitter4j.User user = auth.getAccessor().verifyCredentials();
            service.getRateLimit().addInfo(GetUserMe, user);

            return TwitterMapper.user(user, service);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();

            // ID
            if (id.getId(Long.class).isPresent()) {
                twitter4j.User user = auth.getAccessor().showUser((Long) id.getId());
                service.getRateLimit().addInfo(GetUser, user);
                return TwitterMapper.user(user, service);
            }

            // Screen Name
            if (id.getId(String.class).isPresent()) {
                twitter4j.User user = auth.getAccessor().showUser((String) id.getId());
                service.getRateLimit().addInfo(GetUser, user);
                return TwitterMapper.user(user, service);
            }

            return null;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void followUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().createFriendship((Long) id.getId());
            service.getRateLimit().addInfo(FollowUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unfollowUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().destroyFriendship((Long) id.getId());
            service.getRateLimit().addInfo(UnfollowUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void muteUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().createMute((Long) id.getId());
            service.getRateLimit().addInfo(MuteUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmuteUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().destroyMute((Long) id.getId());
            service.getRateLimit().addInfo(UnmuteUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void blockUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().createBlock((Long) id.getId());
            service.getRateLimit().addInfo(BlockUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unblockUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().destroyBlock((Long) id.getId());
            service.getRateLimit().addInfo(UnblockUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship getRelationship(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();
            ResponseList<Friendship> friendships = auth.getAccessor().lookupFriendships((Long) id.getId());
            service.getRateLimit().addInfo(GetRelationship, friendships);
            return TwitterMapper.relationship(friendships.get(0));
        });
    }

    // ============================================================== //
    // Users
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getFollowingUsers(Identify id, Paging paging) {
        return proceed(() -> {
            Service service = getAccount().getService();

            long cursor = -1;
            int count = 20;
            if (paging != null) {
                if (paging.getCount() != null) {
                    count = paging.getCount().intValue();
                }
                if (paging instanceof CursorPaging) {
                    CursorPaging cpg = (CursorPaging) paging;
                    if (cpg.getCurrentCursor() instanceof Long) {
                        cursor = (long) cpg.getCurrentCursor();
                    }
                }
            }

            PagableResponseList<twitter4j.User> users = auth.getAccessor() //
                    .getFriendsList((Long) id.getId(), cursor, count);

            service.getRateLimit().addInfo(GetFollowingUsers, users);
            return TwitterMapper.users(users, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getFollowerUsers(Identify id, Paging paging) {
        return proceed(() -> {
            Service service = getAccount().getService();

            long cursor = -1;
            int count = 20;
            if (paging != null) {
                if (paging.getCount() != null) {
                    count = paging.getCount().intValue();
                }
                if (paging instanceof CursorPaging) {
                    CursorPaging cpg = (CursorPaging) paging;
                    if (cpg.getCurrentCursor() instanceof Long) {
                        cursor = (long) cpg.getCurrentCursor();
                    }
                }
            }

            PagableResponseList<twitter4j.User> users = auth.getAccessor() //
                    .getFollowersList((Long) id.getId(), cursor, count);

            service.getRateLimit().addInfo(GetFollowerUsers, users);
            return TwitterMapper.users(users, service, paging);
        });
    }

    // ============================================================== //
    // TimeLine
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getHomeTimeLine(Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();
            ResponseList<Status> statues = (paging == null) ? twitter.getHomeTimeline() //
                    : twitter.getHomeTimeline(TwitterMapper.fromPaging(paging));

            service.getRateLimit().addInfo(HomeTimeLine, statues);
            return TwitterMapper.timeLine(statues, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getMentionTimeLine(Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();
            ResponseList<Status> statues = (paging == null) ? twitter.getMentionsTimeline() //
                    : twitter.getMentionsTimeline(TwitterMapper.fromPaging(paging));

            service.getRateLimit().addInfo(MentionTimeLine, statues);
            return TwitterMapper.timeLine(statues, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserCommentTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            ResponseList<Status> statues = null;
            twitter4j.Paging page = (paging == null) ? //
                    null : TwitterMapper.fromPaging(paging);

            // ID
            if (id.getId(Long.class).isPresent()) {
                statues = (paging == null) ? //
                        twitter.getUserTimeline((Long) id.getId()) //
                        : twitter.getUserTimeline((Long) id.getId(), page);
            }
            // Screen Name
            if (id.getId(String.class).isPresent()) {
                statues = (paging == null) ? //
                        twitter.getUserTimeline((String) id.getId()) //
                        : twitter.getUserTimeline((String) id.getId(), page);
            }

            if (statues != null) {
                service.getRateLimit().addInfo(UserCommentTimeLine, statues);
                return TwitterMapper.timeLine(statues, service, paging);
            }
            throw new IllegalStateException();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserLikeTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            ResponseList<Status> statues = null;
            twitter4j.Paging page = (paging == null) ? //
                    null : TwitterMapper.fromPaging(paging);

            // ID
            if (id.getId(Long.class).isPresent()) {
                statues = (paging == null) ? //
                        twitter.getFavorites((Long) id.getId()) //
                        : twitter.getFavorites((Long) id.getId(), page);
            }
            // Screen Name
            if (id.getId(String.class).isPresent()) {
                statues = (paging == null) ? //
                        twitter.getFavorites((String) id.getId()) //
                        : twitter.getFavorites((String) id.getId(), page);
            }

            if (statues != null) {
                service.getRateLimit().addInfo(UserLikeTimeLine, statues);
                return TwitterMapper.timeLine(statues, service, paging);
            }
            throw new IllegalStateException();
        });
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserMediaTimeLine(Identify id, Paging paging) {
        return proceed(() -> {

            int requestCount = 0;
            int maxMediaCount = 50;
            if (paging != null && paging.getCount() != null) {
                maxMediaCount = paging.getCount().intValue();
            }

            // リクエスト時の PagingCount は 200 に固定
            // (メディアが存在しない場合もあるので多めに取得)
            Paging mediaPaging = (paging != null) ? paging : new Paging();
            mediaPaging.setCount(200L);

            Paging storedCursor = paging;
            Paging currentCursor = paging;
            List<Comment> comments = new ArrayList<>();


            // 順にリクエストする必要があるのでループを実行
            while ((requestCount < 10) && (comments.size() <= maxMediaCount)) {

                storedCursor = currentCursor;
                Pageable<Comment> results = getUserCommentTimeLine(id, currentCursor);
                currentCursor = results.pastPage();
                requestCount++;

                for (Comment comment : results.getEntities()) {
                    if (comment.getMedias().size() > 0) {
                        if (comment.getMedias().stream().anyMatch((e) -> //
                                (e.getType() == MediaType.Image) || //
                                        (e.getType() == MediaType.Movie))) {

                            // メディアコメントの場合
                            comments.add(comment);
                        }
                    }
                }
            }

            Pageable<Comment> pageable = new Pageable<>();
            pageable.setPaging(storedCursor);
            pageable.setEntities(comments);
            return pageable;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getSearchTimeLine(String query, Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            Query q = TwitterMapper.queryFromPaging(paging).query(query);
            QueryResult result = twitter.search(q);

            service.getRateLimit().addInfo(SearchTimeLine, result);
            return TwitterMapper.timeLine(result, service, paging);
        });
    }

    // ============================================================== //
    // Comment
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Comment getComment(Identify id) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.showStatus((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(GetComment, status);

            return TwitterMapper.comment(status, service);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void like(Identify id) {
        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.favorites().createFavorite((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(LikeComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlike(Identify id) {
        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.favorites().destroyFavorite((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(UnlikeComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void retweet(Identify id) {
        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.tweets().retweetStatus((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(ShareComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unretweet(Identify id) {
        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.tweets().destroyStatus((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(DeleteComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reaction(Identify id, String reaction) {
        if (reaction != null && !reaction.isEmpty()) {
            String type = reaction.toLowerCase();

            if (TwitterReactionType.Favorite.getCode().contains(type)) {
                like(id);
                return;
            }
            if (TwitterReactionType.Retweet.getCode().contains(type)) {
                retweet(id);
                return;
            }
        }
        throw new NotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unreaction(Identify id, String reaction) {
        if (reaction != null && !reaction.isEmpty()) {
            String type = reaction.toLowerCase();

            if (type.equals(TwitterReactionType.Favorite.getCode())) {
                unlike(id);
                return;
            }
            if (type.equals(TwitterReactionType.Retweet.getCode())) {
                unretweet(id);
                return;
            }
        }
        throw new NotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReactionCandidate> getReactionCandidates() {
        return TwitterMapper.reactionCandidates();
    }

    // ============================================================== //
    // Utils
    // ============================================================== //

    private <T> T proceed(ActionCaller<T, TwitterException> runner) {
        try {
            return runner.proceed();
        } catch (TwitterException e) {
            handleTwitterException(e);
            return null;
        }
    }

    private void proceed(ActionRunner<TwitterException> runner) {
        try {
            runner.proceed();
        } catch (TwitterException e) {
            handleTwitterException(e);
        }
    }

    private static void handleTwitterException(TwitterException e) {
        System.out.println(e.getMessage());
    }

    //region // Getter&Setter
    TwitterAction(Account account, ServiceAuth<Twitter> auth) {
        this.account(account);
        this.auth(auth);
    }

    TwitterAction auth(ServiceAuth<Twitter> auth) {
        this.auth = auth;
        return this;
    }
    //endregion
}
