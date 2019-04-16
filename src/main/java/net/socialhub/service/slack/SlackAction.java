package net.socialhub.service.slack;

import com.github.seratch.jslack.api.methods.request.channels.ChannelsListRequest;
import com.github.seratch.jslack.api.methods.request.reactions.ReactionsAddRequest;
import com.github.seratch.jslack.api.methods.request.reactions.ReactionsRemoveRequest;
import com.github.seratch.jslack.api.methods.request.team.TeamInfoRequest;
import com.github.seratch.jslack.api.methods.request.users.UsersIdentityRequest;
import com.github.seratch.jslack.api.methods.request.users.UsersInfoRequest;
import com.github.seratch.jslack.api.methods.response.channels.ChannelsListResponse;
import com.github.seratch.jslack.api.methods.response.reactions.ReactionsAddResponse;
import com.github.seratch.jslack.api.methods.response.reactions.ReactionsRemoveResponse;
import com.github.seratch.jslack.api.methods.response.team.TeamInfoResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersIdentityResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersInfoResponse;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.service.Channel;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.SlackTeam;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;
import net.socialhub.service.slack.SlackAuth.SlackAccessor;

/**
 * Slack Actions
 */
public class SlackAction extends AccountActionImpl {

    private static Logger logger = Logger.getLogger(SlackAction.class);

    private ServiceAuth<SlackAccessor> auth;

    /** Cached team info */
    private SlackTeam team;

    /** Cached General Channel Id */
    private String generalChannelId;

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
            UsersIdentityResponse identity = auth.getAccessor().getSlack() //
                    .methods().usersIdentity(UsersIdentityRequest.builder() //
                            .token(auth.getAccessor().getToken()) //
                            .build());

            UsersInfoResponse account = auth.getAccessor().getSlack() //
                    .methods().usersInfo(UsersInfoRequest.builder() //
                            .token(auth.getAccessor().getToken()) //
                            .user(identity.getUser().getId()) //
                            .build());

            return SlackMapper.user(account, getTeam(), service);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();
            UsersInfoResponse account = auth.getAccessor().getSlack() //
                    .methods().usersInfo(UsersInfoRequest.builder() //
                            .token(auth.getAccessor().getToken()) //
                            .user((String) id.getId()) //
                            .build());

            return SlackMapper.user(account, getTeam(), service);
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
            ReactionsAddResponse response = auth.getAccessor().getSlack() //
                    .methods().reactionsAdd(ReactionsAddRequest.builder() //
                            .token(auth.getAccessor().getToken()) //
                            .channel((String) id.getId()) //
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlike(Identify id) {
        proceed(() -> {
            ReactionsRemoveResponse response = auth.getAccessor().getSlack() //
                    .methods().reactionsRemove(ReactionsRemoveRequest.builder() //
                            .token(auth.getAccessor().getToken()) //
                            .channel((String) id.getId()) //
                            .build());
        });
    }

    // ============================================================== //
    // Channels
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Channel> getChannels() {
        return proceed(() -> {
            Service service = getAccount().getService();
            ChannelsListResponse response = auth.getAccessor().getSlack() //
                    .methods().channelsList(ChannelsListRequest.builder() //
                            .token(auth.getAccessor().getToken()) //
                            .build());

            // General のチャンネルを記録
            response.getChannels().stream() //
                    .filter((c) -> c.isGeneral()).findFirst() //
                    .map((c) -> generalChannelId = c.getId());

            return SlackMapper.channel(response, service);
        });
    }

    // ============================================================== //
    // Only Slack Action
    // ============================================================== //

    /**
     * Get Team Information
     * (Return cached information if already requested)
     * チーム情報を返却 (既にリクエスト済みの場合はキャッシュを返す)
     */
    public SlackTeam getTeam() {
        if (this.team != null) {
            return this.team;
        }

        return proceed(() -> {
            TeamInfoResponse team = auth.getAccessor().getSlack() //
                    .methods().teamInfo(TeamInfoRequest.builder() //
                            .token(auth.getAccessor().getToken()) //
                            .build());

            this.team = SlackMapper.team(team);
            return this.team;
        });
    }

    // ============================================================== //
    // Utils
    // ============================================================== //

    /**
     * General マークされたチャンネルを取得
     */
    public String getGeneralChannelId() {

        // 不明な場合はチャンネル一覧を先に取得
        if (generalChannelId == null) {
            getChannels();
        }
        return generalChannelId;
    }

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
