package net.socialhub.service.mastodon;

import mastodon4j.Mastodon;
import mastodon4j.entity.Relationship;
import mastodon4j.entity.Status;
import mastodon4j.entity.share.Response;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.service.*;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;

import static net.socialhub.define.ActionEnum.*;

public class MastodonAction extends AccountActionImpl {

    private static Logger logger = Logger.getLogger(MastodonAction.class);

    private ServiceAuth<Mastodon> auth;

    // ============================================================== //
    // Account
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserMe() {
        return proceed(() -> {
            Mastodon mastodon = auth.getToken();
            Service service = getAccount().getService();
            Response<mastodon4j.entity.Account> account = mastodon.verifyCredentials();

            service.getRateLimit().addInfo(GetUserMe, account);
            return MastodonMapper.user(account.get(), service);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            Mastodon mastodon = auth.getToken();
            Service service = getAccount().getService();
            Response<mastodon4j.entity.Account> account = mastodon.getAccount(id.getNumberId());

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
            Mastodon mastodon = auth.getToken();
            Service service = getAccount().getService();
            Response<Relationship> relationship = mastodon.follow(id.getNumberId());

            service.getRateLimit().addInfo(FollowUser, relationship);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unfollowUser(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getToken();
            Service service = getAccount().getService();
            Response<Relationship> relationship = mastodon.unfollow(id.getNumberId());

            service.getRateLimit().addInfo(UnfollowUser, relationship);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void muteUser(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getToken();
            Service service = getAccount().getService();
            Response<Relationship> relationship = mastodon.mute(id.getNumberId());

            service.getRateLimit().addInfo(MuteUser, relationship);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmuteUser(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getToken();
            Service service = getAccount().getService();
            Response<Relationship> relationship = mastodon.unmute(id.getNumberId());

            service.getRateLimit().addInfo(UnmuteUser, relationship);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void blockUser(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getToken();
            Service service = getAccount().getService();
            Response<Relationship> relationship = mastodon.block(id.getNumberId());

            service.getRateLimit().addInfo(BlockUser, relationship);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unblockUser(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getToken();
            Service service = getAccount().getService();
            Response<Relationship> relationship = mastodon.unblock(id.getNumberId());

            service.getRateLimit().addInfo(UnblockUser, relationship);
        });
    }

    // ============================================================== //
    // Comment
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getHomeTimeLine(Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getToken();
            Service service = getAccount().getService();
            Response<Status[]> status = mastodon.getHomeTimeline();

            service.getRateLimit().addInfo(HomeTimeLine, status);
            return MastodonMapper.timeLine(status.get(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void like(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getToken();
            Service service = getAccount().getService();
            Response<Status> status = mastodon.statuses().favourite(id.getNumberId());

            service.getRateLimit().addInfo(LikeComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlike(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getToken();
            Service service = getAccount().getService();
            Response<Status> status = mastodon.statuses().unfavourite(id.getNumberId());

            service.getRateLimit().addInfo(UnlikeComment, status);
        });
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

    //<editor-fold desc="// Getter&Setter">
    MastodonAction(Account account, ServiceAuth<Mastodon> auth) {
        this.account(account);
        this.auth(auth);
    }

    MastodonAction auth(ServiceAuth<Mastodon> auth) {
        this.auth = auth;
        return this;
    }
    //</editor-fold>
}
