package net.socialhub.service.slack;

import com.github.seratch.jslack.api.methods.request.reactions.ReactionsAddRequest;
import com.github.seratch.jslack.api.methods.request.reactions.ReactionsRemoveRequest;
import com.github.seratch.jslack.api.methods.request.users.UsersIdentityRequest;
import com.github.seratch.jslack.api.methods.request.users.UsersInfoRequest;
import com.github.seratch.jslack.api.methods.response.reactions.ReactionsAddResponse;
import com.github.seratch.jslack.api.methods.response.reactions.ReactionsRemoveResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersIdentityResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersInfoResponse;
import net.socialhub.define.service.SlackConstants;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;
import net.socialhub.service.slack.SlackAuth.SlackAccessor;

/**
 * Slack Actions
 */
public class SlackAction extends AccountActionImpl {

    private static Logger logger = Logger.getLogger(SlackAction.class);

    private ServiceAuth<SlackAccessor> auth;

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
            UsersIdentityResponse account = auth.getToken().getSlack() //
                    .methods().usersIdentity(UsersIdentityRequest.builder() //
                            .token(auth.getToken().getToken()).build());

            return SlackMapper.user(account, service);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();
            UsersInfoResponse account = auth.getToken().getSlack() //
                    .methods().usersInfo(UsersInfoRequest.builder() //
                            .token(auth.getToken().getToken()).user(id.getStringId()).build());

            return SlackMapper.user(account, service);
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
            Service service = getAccount().getService();
            ReactionsAddResponse response = auth.getToken().getSlack() //
                    .methods().reactionsAdd(ReactionsAddRequest.builder() //
                            .channel(id.getInfo(SlackConstants.CHANNEL_KEY)) //
                            .token(auth.getToken().getToken()).build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlike(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            ReactionsRemoveResponse response = auth.getToken().getSlack() //
                    .methods().reactionsRemove(ReactionsRemoveRequest.builder() //
                            .channel(id.getInfo(SlackConstants.CHANNEL_KEY)) //
                            .token(auth.getToken().getToken()).build());
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

    //region // Getter&Setter
    SlackAction(Account account, ServiceAuth<SlackAccessor> auth) {
        this.account(account);
        this.auth(auth);
    }

    SlackAction auth(ServiceAuth<SlackAccessor> auth) {
        this.auth = auth;
        return this;
    }
    //endregion

}
