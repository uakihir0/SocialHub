package net.socialhub.service.mastodon;

import mastodon4j.Mastodon;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.SuperAccountAction;

public class MastodonAction extends SuperAccountAction {

    private static Logger logger = Logger.getLogger(MastodonAction.class);

    private ServiceAuth<Mastodon> auth;

    // ============================================================== //
    // Account
    // ============================================================== //

    @Override
    public User getUserMe() {
        return proceed(() -> {
            Service service = getAccount().getService();
            mastodon4j.entity.Account account = auth.getToken().verifyCredentials();

            return MastodonMapper.user(account, service);
        });
    }

    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();
            mastodon4j.entity.Account account = auth.getToken().getAccount(id.getNumberId());

            return MastodonMapper.user(account, service);
        });
    }

    @Override
    public void followUser(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getToken();
            mastodon.follow(id.getNumberId());
        });
    }

    @Override
    public void unfollowUser(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getToken();
            mastodon.unfollow(id.getNumberId());
        });
    }

    // ============================================================== //
    // Comment
    // ============================================================== //

    @Override
    public Pageable<Comment> getHomeTimeLine(Paging paging) {
        return proceed(() -> {
            return null;
        });
    }

    @Override
    public void like(Identify id){
        proceed(() ->{
            Mastodon mastodon = auth.getToken();
            mastodon.statuses().favourite(id.getNumberId());
        });
    }

    @Override
    public void unlike(Identify id){
        proceed(() ->{
            Mastodon mastodon = auth.getToken();
            mastodon.statuses().unfavourite(id.getNumberId());
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
