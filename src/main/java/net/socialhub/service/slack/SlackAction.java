package net.socialhub.service.slack;

import com.github.seratch.jslack.api.methods.request.channels.ChannelsHistoryRequest;
import com.github.seratch.jslack.api.methods.request.channels.ChannelsHistoryRequest.ChannelsHistoryRequestBuilder;
import com.github.seratch.jslack.api.methods.request.channels.ChannelsListRequest;
import com.github.seratch.jslack.api.methods.request.emoji.EmojiListRequest;
import com.github.seratch.jslack.api.methods.request.reactions.ReactionsAddRequest;
import com.github.seratch.jslack.api.methods.request.reactions.ReactionsRemoveRequest;
import com.github.seratch.jslack.api.methods.request.team.TeamInfoRequest;
import com.github.seratch.jslack.api.methods.request.users.UsersIdentityRequest;
import com.github.seratch.jslack.api.methods.request.users.UsersInfoRequest;
import com.github.seratch.jslack.api.methods.response.channels.ChannelsHistoryResponse;
import com.github.seratch.jslack.api.methods.response.channels.ChannelsListResponse;
import com.github.seratch.jslack.api.methods.response.emoji.EmojiListResponse;
import com.github.seratch.jslack.api.methods.response.reactions.ReactionsAddResponse;
import com.github.seratch.jslack.api.methods.response.reactions.ReactionsRemoveResponse;
import com.github.seratch.jslack.api.methods.response.team.TeamInfoResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersIdentityResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersInfoResponse;
import com.github.seratch.jslack.api.model.Message;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.error.SocialHubException;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.slack.SlackComment;
import net.socialhub.model.service.addition.slack.SlackIdentify;
import net.socialhub.model.service.addition.slack.SlackTeam;
import net.socialhub.model.service.paging.DatePaging;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;
import net.socialhub.service.slack.SlackAuth.SlackAccessor;
import net.socialhub.utils.LimitMap;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Slack Actions
 */
public class SlackAction extends AccountActionImpl {

    private static Logger logger = Logger.getLogger(SlackAction.class);

    private ServiceAuth<SlackAccessor> auth;

    /** Cached team info */
    private SlackTeam team;

    /** Cached General Channel Id */
    private String generalChannel;

    /** User Cache Data */
    private Map<String, User> userCache = Collections.synchronizedMap(new LimitMap<>(200));

    /** Reaction Candidate Cache */
    private List<ReactionCandidate> reactionCandidate;

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

            return userCache(SlackMapper.user(account, getTeam(), service));
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

            return userCache(SlackMapper.user(account, getTeam(), service));
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
        reaction(id, "heart");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlike(Identify id) {
        unreaction(id, "heart");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reaction(Identify id, String reaction) {
        proceed(() -> {
            ReactionsAddResponse response = auth.getAccessor().getSlack() //
                    .methods().reactionsAdd(ReactionsAddRequest.builder() //
                            .token(auth.getAccessor().getToken()) //
                            .timestamp((String) id.getId()) //
                            .channel(getChannelId(id)) //
                            .name(reaction) //
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unreaction(Identify id, String reaction) {
        proceed(() -> {
            ReactionsRemoveResponse response = auth.getAccessor().getSlack() //
                    .methods().reactionsRemove(ReactionsRemoveRequest.builder() //
                            .token(auth.getAccessor().getToken()) //
                            .timestamp((String) id.getId()) //
                            .channel(getChannelId(id)) //
                            .name(reaction) //
                            .build());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReactionCandidate> getReactionCandidates() {
        if (this.reactionCandidate != null) {
            return this.reactionCandidate;
        }

        return proceed(() -> {
            EmojiListResponse response = auth.getAccessor().getSlack() //
                    .methods().emojiList(EmojiListRequest.builder() //
                            .token(auth.getAccessor().getToken())
                            .build());

            this.reactionCandidate = SlackMapper.reactionCandidates(response);
            return this.reactionCandidate;
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
            ExecutorService pool = Executors.newCachedThreadPool();

            // ------------------------------------------------ //
            // Async Request
            // ------------------------------------------------ //

            Future<ChannelsHistoryResponse> responseFuture = pool.submit(() -> {
                ChannelsHistoryRequestBuilder request = ChannelsHistoryRequest.builder() //
                        .channel(getGeneralChannel()).token(auth.getAccessor().getToken());

                if (paging != null) {
                    if (paging.getCount() != null) {
                        request.count(paging.getCount().intValue());
                    }

                    if (paging instanceof DatePaging) {
                        DatePaging date = (DatePaging) paging;
                        if (date.getLatest() != null) {
                            request.latest(date.getLatest());
                        }
                        if (date.getOldest() != null) {
                            request.oldest(date.getOldest());
                        }
                        if (date.getInclusive() != null) {
                            request.inclusive(date.getInclusive());
                        }
                    }
                }

                return auth.getAccessor().getSlack().methods() //
                        .channelsHistory(request.build());
            });

            Future<List<ReactionCandidate>> candidatesFuture = //
                    pool.submit(this::getReactionCandidates);

            Future<SlackTeam> teamFuture = pool.submit(this::getTeam);

            // ------------------------------------------------ //
            // Await Request
            // ------------------------------------------------ //

            ChannelsHistoryResponse response = responseFuture.get();
            List<ReactionCandidate> candidates = candidatesFuture.get();
            teamFuture.get();

            // ------------------------------------------------ //
            // Get User Instances
            // ------------------------------------------------ //

            Service service = getAccount().getService();
            List<String> users = response.getMessages().stream() //
                    .map(Message::getUser).filter(Objects::nonNull) //
                    .distinct().collect(Collectors.toList());

            Map<String, User> userMap = users.parallelStream() //
                    .collect(Collectors.toMap(Function.identity(), //
                            (id) -> getUserWithCache(new Identify(service, id))));

            return SlackMapper.timeLine(response, userMap, candidates, //
                    getGeneralChannel(), service, paging);
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
                    .map((c) -> generalChannel = c.getId());

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
    public String getGeneralChannel() {

        // 不明な場合はチャンネル一覧を先に取得
        if (generalChannel == null) {
            getChannels();
        }
        return generalChannel;
    }

    // ============================================================== //
    // Support
    // ============================================================== //

    private String timeStampString(Date date) {
        long number = date.getTime() / 1000L;
        return Long.toString(number);
    }

    private String getChannelId(Identify id) {
        if (id instanceof SlackComment) {
            return ((SlackComment) id).getChannel();
        }
        if (id instanceof SlackIdentify) {
            return ((SlackIdentify) id).getChannel();
        }

        String message = "No Channel Info. Identify must be SlackIdentify or SlackComment.";
        throw new SocialHubException(message);
    }

    // ============================================================== //
    // Cache
    // ============================================================== //

    private User userCache(User user) {
        user.getId(String.class).ifPresent( //
                (id) -> userCache.put(id, user));
        return user;
    }

    private User getUserWithCache(Identify id) {
        String time = id.getId(String.class).orElse(null);
        if (time != null && userCache.containsKey(time)) {
            return userCache.get(time);
        }
        return getUser(id);
    }


    // ============================================================== //
    // Proceed
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

    public SlackAction auth(ServiceAuth<SlackAccessor> auth) {
        this.auth = auth;
        return this;
    }

    public ServiceAuth<SlackAccessor> getAuth() {
        return auth;
    }
    //endregion

}
