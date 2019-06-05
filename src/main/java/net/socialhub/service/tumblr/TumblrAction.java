package net.socialhub.service.tumblr;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.exceptions.JumblrException;
import net.socialhub.model.Account;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;

public class TumblrAction extends AccountActionImpl {

    private ServiceAuth<JumblrClient> auth;

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
            com.tumblr.jumblr.types.User user = auth.getAccessor().user();

            return TumblrMapper.user(user, service);
        });
    }

    // ============================================================== //
    // Utils
    // ============================================================== //

    private <T> T proceed(ActionCaller<T, JumblrException> runner) {
        try {
            return runner.proceed();
        } catch (JumblrException e) {
            handleTumblrException(e);
            return null;
        }
    }

    private void proceed(ActionRunner<JumblrException> runner) {
        try {
            runner.proceed();
        } catch (JumblrException e) {
            handleTumblrException(e);
        }
    }

    private static void handleTumblrException(JumblrException e) {
        System.out.println(e.getMessage());
    }

    //region // Getter&Setter
    TumblrAction(Account account, ServiceAuth<JumblrClient> auth) {
        this.account(account);
        this.auth(auth);
    }

    TumblrAction auth(ServiceAuth<JumblrClient> auth) {
        this.auth = auth;
        return this;
    }
    //endregion
}
