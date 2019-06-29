package net.socialhub.service.facebook;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;

public class FacebookAction extends AccountActionImpl {

    private static Logger logger = Logger.getLogger(FacebookAuth.class);

    private ServiceAuth<Facebook> auth;

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
            facebook4j.User user = auth.getAccessor().users().getMe();

            return FacebookMapper.user(user, service);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();
            facebook4j.User user = auth.getAccessor().users().getUser((String) id.getId());

            return FacebookMapper.user(user, service);
        });
    }

    // ============================================================== //
    // Comment
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public void like(Identify id) {
        proceed(() -> {
            auth.getAccessor().comments().likeComment((String) id.getId());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlike(Identify id) {
        proceed(() -> {
            auth.getAccessor().comments().unlikeComment((String) id.getId());
        });
    }

    // ============================================================== //
    // Utils
    // ============================================================== //

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

    //region // Getter&Setter
    FacebookAction(Account account, ServiceAuth<Facebook> auth) {
        this.account(account);
        this.auth(auth);
    }

    FacebookAction auth(ServiceAuth<Facebook> auth) {
        this.auth = auth;
        return this;
    }
    //endregion
}
