package net.socialhub.service.facebook;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.error.NotSupportedException;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountAction;
import net.socialhub.service.action.SuperAccountAction;
import net.socialhub.service.action.SuperUserAction;

public class FacebookAction extends SuperAccountAction {

    private static Logger logger = Logger.getLogger(FacebookAuth.class);

    private ServiceAuth<Facebook> auth;

    // ============================================================== //
    // Account
    // ============================================================== //

    @Override
    public User getUserMe() {
        return proceed(() -> {
            Service service = getAccount().getService();
            facebook4j.User user = auth.getToken().users().getMe();

            return FacebookMapper.user(user, service);
        });
    }

    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();
            facebook4j.User user = auth.getToken().users().getUser(id.getStringId());

            return FacebookMapper.user(user, service);
        });
    }

    @Override
    public void followUser(Identify id) {
        throw new NotSupportedException();
    }

    @Override
    public void unfollowUser(Identify id) {
        throw new NotSupportedException();
    }

    // ============================================================== //
    // Comment
    // ============================================================== //
    @Override
    public void like(Identify id) {
        proceed(() -> {
            auth.getToken().comments().likeComment(id.getStringId());
        });
    }

    @Override
    public void unlike(Identify id) {
        proceed(() -> {
            auth.getToken().comments().unlikeComment(id.getStringId());
        });
    }


    /**
     * ==============================================================
     * User Targeted Actions
     * ==============================================================
     */
    public static class SHFacebookUserAction extends SuperUserAction {

        public SHFacebookUserAction(AccountAction action) {
            super(action);
        }

        @Override
        public void followUser() {
            throw new NotSupportedException();
        }
    }

    private <T> T proceed(ActionCaller<T, FacebookException> runner) {
        try {
            return runner.proceed();
        } catch (FacebookException e) {
            handleFacebookException(e);
            return null;
        }
    }

    private void proceed(ActionRunner<FacebookException> runner) {
        try {
            runner.proceed();
        } catch (FacebookException e) {
            handleFacebookException(e);
        }
    }

    private static void handleFacebookException(FacebookException e) {
        logger.debug(e.getMessage(), e);
    }

    //<editor-fold desc="// Getter&Setter">
    FacebookAction(Account account, ServiceAuth<Facebook> auth) {
        this.account(account);
        this.auth(auth);
    }

    FacebookAction auth(ServiceAuth<Facebook> auth) {
        this.auth = auth;
        return this;
    }
    //</editor-fold>
}
