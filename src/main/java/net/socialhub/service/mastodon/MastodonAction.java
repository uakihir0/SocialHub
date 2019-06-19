package net.socialhub.service.mastodon;

import mastodon4j.Mastodon;
import mastodon4j.Range;
import mastodon4j.entity.Notification;
import mastodon4j.entity.Relationship;
import mastodon4j.entity.Status;
import mastodon4j.entity.share.Response;
import net.socialhub.define.service.mastodon.MastodonNotificationType;
import net.socialhub.define.service.mastodon.MastodonReactionType;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.error.NotSupportedException;
import net.socialhub.model.service.*;
import net.socialhub.model.service.paging.BorderPaging;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.socialhub.define.action.OtherActionType.*;
import static net.socialhub.define.action.TimeLineActionType.*;

public class MastodonAction extends AccountActionImpl {

    private static Logger logger = Logger.getLogger(MastodonAction.class);

    private ServiceAuth<Mastodon> auth;

    // 自分のアカウント ID を記録
    private Identify myAccountId;

    // ============================================================== //
    // Account
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserMe() {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<mastodon4j.entity.Account> account = mastodon.verifyCredentials();

            service.getRateLimit().addInfo(GetUserMe, account);
            User me = MastodonMapper.user(account.get(), service);
            myAccountId = me;
            return me;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<mastodon4j.entity.Account> account = mastodon.getAccount((Long) id.getId());

            service.getRateLimit().addInfo(GetUser, account);
            return MastodonMapper.user(account.get(), service);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void followUser(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<Relationship> relationship = mastodon.follow((Long) id.getId());

            service.getRateLimit().addInfo(FollowUser, relationship);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unfollowUser(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<Relationship> relationship = mastodon.unfollow((Long) id.getId());

            service.getRateLimit().addInfo(UnfollowUser, relationship);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void muteUser(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<Relationship> relationship = mastodon.mute((Long) id.getId());

            service.getRateLimit().addInfo(MuteUser, relationship);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmuteUser(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<Relationship> relationship = mastodon.unmute((Long) id.getId());

            service.getRateLimit().addInfo(UnmuteUser, relationship);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void blockUser(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<Relationship> relationship = mastodon.block((Long) id.getId());

            service.getRateLimit().addInfo(BlockUser, relationship);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unblockUser(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<Relationship> relationship = mastodon.unblock((Long) id.getId());

            service.getRateLimit().addInfo(UnblockUser, relationship);
        });
    }


    // ============================================================== //
    // User API
    // ユーザー関連 API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getFollowingUsers(Identify id, Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Range range = getRange(paging);

            Response<mastodon4j.entity.Account[]> accounts = //
                    mastodon.accounts().getFollowing((Long) id.getId(), range);

            service.getRateLimit().addInfo(GetFollowingUsers, accounts);
            return MastodonMapper.users(accounts.get(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getFollowerUsers(Identify id, Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Range range = getRange(paging);

            Response<mastodon4j.entity.Account[]> accounts = //
                    mastodon.accounts().getFollowers((Long) id.getId(), range);

            service.getRateLimit().addInfo(GetFollowerUsers, accounts);
            return MastodonMapper.users(accounts.get(), service, paging);
        });
    }

    // ============================================================== //
    // Timeline
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getHomeTimeLine(Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Range range = getRange(paging);

            Response<Status[]> status = mastodon.getHomeTimeline(range);
            service.getRateLimit().addInfo(HomeTimeLine, status);
            return MastodonMapper.timeLine(status.get(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    public Pageable<Comment> getMentionTimeLine(Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Range range = getRange(paging);


            Response<Notification[]> status =
                    mastodon.notifications() //
                            .getNotifications(range, Arrays.asList( //
                                    MastodonNotificationType.follow.getCode(), //
                                    MastodonNotificationType.favourite.getCode(), //
                                    MastodonNotificationType.reblog.getCode()), //
                                    null);

            List<Status> statuses = Stream.of(status.get())
                    .map(Notification::getStatus)
                    .collect(Collectors.toList());

            service.getRateLimit().addInfo(MentionTimeLine, status);
            return MastodonMapper.timeLine(statuses, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    public Pageable<Comment> getUserCommentTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Range range = getRange(paging);

            Response<Status[]> status = mastodon.accounts().getStatuses( //
                    (Long) id.getId(), false, false, false, false, range);

            service.getRateLimit().addInfo(UserCommentTimeLine, status);
            return MastodonMapper.timeLine(status.get(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserLikeTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            if (id != null) {

                // 自分の分しか取得できないので id が自分でない場合は例外
                if (id.getId().equals(getMyAccountId().getId())) {

                    Mastodon mastodon = auth.getAccessor();
                    Service service = getAccount().getService();
                    Range range = getRange(paging);

                    Response<Status[]> status = mastodon.favourites().getFavourites(range);

                    service.getRateLimit().addInfo(UserLikeTimeLine, status);
                    return MastodonMapper.timeLine(status.get(), service, paging);
                }
            }

            throw new NotSupportedException(
                    "Sorry, user favorites timeline is only support only verified account on Mastodon.");
        });
    }

    /**
     * {@inheritDoc}
     */
    public Pageable<Comment> getUserMediaTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Range range = getRange(paging);

            Response<Status[]> status = mastodon.accounts().getStatuses( //
                    (Long) id.getId(), false, true, false, false, range);

            service.getRateLimit().addInfo(UserMediaTimeLine, status);
            return MastodonMapper.timeLine(status.get(), service, paging);
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
            Mastodon mastodon = auth.getAccessor();
            Response<Status> status = mastodon.statuses().getStatus((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(GetComment, status);
            return MastodonMapper.comment(status.get(), service);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void like(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<Status> status = mastodon.statuses().favourite((Long) id.getId());

            service.getRateLimit().addInfo(LikeComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlike(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<Status> status = mastodon.statuses().unfavourite((Long) id.getId());

            service.getRateLimit().addInfo(UnlikeComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void retweet(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<Status> status = mastodon.statuses().reblog((Long) id.getId());

            service.getRateLimit().addInfo(ShareComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unretweet(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<Status> status = mastodon.statuses().unreblog((Long) id.getId());

            service.getRateLimit().addInfo(UnShareComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reaction(Identify id, String reaction) {
        if (reaction != null && !reaction.isEmpty()) {
            String type = reaction.toLowerCase();

            if (MastodonReactionType.Favorite.getCode().contains(type)) {
                like(id);
                return;
            }
            if (MastodonReactionType.Reblog.getCode().contains(type)) {
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

            if (MastodonReactionType.Favorite.getCode().contains(type)) {
                unlike(id);
                return;
            }
            if (MastodonReactionType.Reblog.getCode().contains(type)) {
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
        return MastodonMapper.reactionCandidates();
    }

    // ============================================================== //
    // Paging
    // ============================================================== //

    private static Range getRange(Paging paging) {
        if (paging == null) {
            return null;
        }

        Range range = new Range();
        range.setLimit(paging.getCount());

        if (paging instanceof BorderPaging) {
            BorderPaging border = (BorderPaging) paging;

            if (border.getSinceId() != null) {
                if (border.getHintNewer() == Boolean.TRUE) {
                    range.setMinId(border.getSinceId());
                } else {
                    range.setSinceId(border.getSinceId());
                }
            }

            if (border.getMaxId() != null) {
                range.setMaxId(border.getMaxId());
            }
        }
        return range;
    }

    // ============================================================== //
    // Cache
    // ============================================================== //

    /**
     * Get My AccountID (with Cache)
     * 自分のアカウント ID を取得
     */
    private Identify getMyAccountId() {
        return (myAccountId != null) ? myAccountId : getUserMe();
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

    private void proceed(ActionRunner<Exception> runner) {
        try {
            runner.proceed();
        } catch (Exception e) {
            handleException(e);
        }
    }

    private static void handleException(Exception e) {
        logger.debug(e.getMessage(), e);
    }

    //region // Getter&Setter
    MastodonAction(Account account, ServiceAuth<Mastodon> auth) {
        this.account(account);
        this.auth(auth);
    }

    MastodonAction auth(ServiceAuth<Mastodon> auth) {
        this.auth = auth;
        return this;
    }
    //endregion
}
