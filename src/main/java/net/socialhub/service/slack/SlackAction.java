package net.socialhub.service.slack;

import com.github.seratch.jslack.api.methods.request.bots.BotsInfoRequest;
import com.github.seratch.jslack.api.methods.request.channels.ChannelsHistoryRequest;
import com.github.seratch.jslack.api.methods.request.channels.ChannelsHistoryRequest.ChannelsHistoryRequestBuilder;
import com.github.seratch.jslack.api.methods.request.channels.ChannelsListRequest;
import com.github.seratch.jslack.api.methods.request.channels.ChannelsRepliesRequest;
import com.github.seratch.jslack.api.methods.request.chat.ChatDeleteRequest;
import com.github.seratch.jslack.api.methods.request.chat.ChatPostMessageRequest;
import com.github.seratch.jslack.api.methods.request.chat.ChatPostMessageRequest.ChatPostMessageRequestBuilder;
import com.github.seratch.jslack.api.methods.request.emoji.EmojiListRequest;
import com.github.seratch.jslack.api.methods.request.files.FilesUploadRequest;
import com.github.seratch.jslack.api.methods.request.files.FilesUploadRequest.FilesUploadRequestBuilder;
import com.github.seratch.jslack.api.methods.request.im.ImListRequest;
import com.github.seratch.jslack.api.methods.request.mpim.MpimListRequest;
import com.github.seratch.jslack.api.methods.request.reactions.ReactionsAddRequest;
import com.github.seratch.jslack.api.methods.request.reactions.ReactionsRemoveRequest;
import com.github.seratch.jslack.api.methods.request.team.TeamInfoRequest;
import com.github.seratch.jslack.api.methods.request.users.UsersIdentityRequest;
import com.github.seratch.jslack.api.methods.request.users.UsersInfoRequest;
import com.github.seratch.jslack.api.methods.response.bots.BotsInfoResponse;
import com.github.seratch.jslack.api.methods.response.channels.ChannelsHistoryResponse;
import com.github.seratch.jslack.api.methods.response.channels.ChannelsListResponse;
import com.github.seratch.jslack.api.methods.response.channels.ChannelsRepliesResponse;
import com.github.seratch.jslack.api.methods.response.chat.ChatDeleteResponse;
import com.github.seratch.jslack.api.methods.response.emoji.EmojiListResponse;
import com.github.seratch.jslack.api.methods.response.im.ImListResponse;
import com.github.seratch.jslack.api.methods.response.mpim.MpimListResponse;
import com.github.seratch.jslack.api.methods.response.reactions.ReactionsAddResponse;
import com.github.seratch.jslack.api.methods.response.reactions.ReactionsRemoveResponse;
import com.github.seratch.jslack.api.methods.response.team.TeamInfoResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersIdentityResponse;
import com.github.seratch.jslack.api.methods.response.users.UsersInfoResponse;
import com.github.seratch.jslack.api.model.Message;
import net.socialhub.define.service.slack.SlackFormKey;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.error.SocialHubException;
import net.socialhub.model.request.CommentForm;
import net.socialhub.model.request.MediaForm;
import net.socialhub.model.service.Thread;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.slack.SlackComment;
import net.socialhub.model.service.addition.slack.SlackIdentify;
import net.socialhub.model.service.addition.slack.SlackTeam;
import net.socialhub.model.service.paging.CursorPaging;
import net.socialhub.model.service.paging.DatePaging;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;
import net.socialhub.service.action.RequestAction;
import net.socialhub.service.slack.SlackAuth.SlackAccessor;
import net.socialhub.utils.LimitMap;
import net.socialhub.utils.MapperUtil;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.socialhub.define.service.slack.SlackMessageSubType.BotMessage;

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

    /** Account Cache Data */
    private Map<String, User> userCache = Collections.synchronizedMap(new LimitMap<>(200));
    private Map<String, User> botCache = Collections.synchronizedMap(new LimitMap<>(10));

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

            me = userCache(SlackMapper.user(account, getTeam(), service));
            return me;
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
    public void postComment(CommentForm req) {
        proceed(() -> {
            String token = auth.getAccessor().getToken();
            String channel = (String) req.getParams().get(SlackFormKey.CHANNEL_KEY);

            if (req.getImages() != null && !req.getImages().isEmpty()) {

                if (req.getImages().size() > 1) {
                    for (MediaForm media : req.getImages()) {

                        // 複数のファイルを先に個々にアップロード
                        FilesUploadRequestBuilder builder = //
                                FilesUploadRequest.builder() //
                                        .channels(Collections.singletonList(channel)) //
                                        .filestream(new ByteArrayInputStream(media.getData())) //
                                        .filename(media.getName());

                        if (req.getReplyId() != null) {
                            builder.threadTs((String) req.getReplyId());
                        }

                        auth.getAccessor().getSlack().methods() //
                                .filesUpload(builder.token(token).build());
                    }
                    {
                        // 最後にコメントを投稿
                        ChatPostMessageRequestBuilder builder = //
                                ChatPostMessageRequest.builder() //
                                        .text(req.getMessage()) //
                                        .channel(channel);

                        if (req.getReplyId() != null) {
                            builder.threadTs((String) req.getReplyId());
                        }

                        auth.getAccessor().getSlack().methods() //
                                .chatPostMessage(builder.token(token).build());
                    }

                } else {

                    // メディアは一つだけの場合はそれだけを投稿
                    MediaForm media = req.getImages().get(0);
                    FilesUploadRequestBuilder builder = //
                            FilesUploadRequest.builder() //
                                    .initialComment(req.getMessage()) //
                                    .channels(Collections.singletonList(channel)) //
                                    .filestream(new ByteArrayInputStream(media.getData())) //
                                    .filename(media.getName());

                    if (req.getReplyId() != null) {
                        builder.threadTs((String) req.getReplyId());
                    }

                    auth.getAccessor().getSlack().methods() //
                            .filesUpload(builder.token(token).build());
                }

            } else {

                // コメントだけの場合
                ChatPostMessageRequestBuilder builder = //
                        ChatPostMessageRequest.builder() //
                                .text(req.getMessage()) //
                                .channel(channel);

                if (req.getReplyId() != null) {
                    builder.threadTs((String) req.getReplyId());
                }

                auth.getAccessor().getSlack().methods() //
                        .chatPostMessage(builder.token(token).build());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void likeComment(Identify id) {
        reactionComment(id, "heart");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlikeComment(Identify id) {
        unreactionComment(id, "heart");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reactionComment(Identify id, String reaction) {
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
    public void unreactionComment(Identify id, String reaction) {
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
    public void deleteComment(Identify id) {
        proceed(() -> {
            ChatDeleteResponse response = auth.getAccessor().getSlack() //
                    .methods().chatDelete(ChatDeleteRequest.builder() //
                            .token(auth.getAccessor().getToken()) //
                            .channel(getChannelId(id)) //
                            .ts((String) id.getId()) //
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
                            .token(auth.getAccessor().getToken()).build());

            this.reactionCandidate = SlackMapper.reactionCandidates(response);
            return this.reactionCandidate;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getCommentContext(Identify id) {
        return proceed(() -> {
            ExecutorService pool = Executors.newCachedThreadPool();

            // ------------------------------------------------ //
            // Async Request
            // ------------------------------------------------ //

            Future<ChannelsRepliesResponse> responseFuture = pool.submit(() -> {

                // スレッドID を取得する (SlackComment であることを期待)
                String threadId = (id instanceof SlackComment) ? //
                        ((SlackComment) id).getThreadId() : (String) id.getId();

                return auth.getAccessor().getSlack() //
                        .methods().channelsReplies(ChannelsRepliesRequest.builder() //
                                .token(auth.getAccessor().getToken()) //
                                .channel(getChannelId(id)) //
                                .threadTs(threadId) //
                                .build());
            });

            Future<List<ReactionCandidate>> candidatesFuture = //
                    pool.submit(this::getReactionCandidates);

            Future<User> userMeFuture = pool.submit(this::getUserMeWithCache);

            // ------------------------------------------------ //
            // Await Request
            // ------------------------------------------------ //

            ChannelsRepliesResponse response = responseFuture.get();
            List<ReactionCandidate> candidates = candidatesFuture.get();
            User userMe = userMeFuture.get();

            // ------------------------------------------------ //
            // Get User Instances
            // ------------------------------------------------ //

            Service service = getAccount().getService();
            List<String> users = response.getMessages().stream() //
                    .map(Message::getUser).filter(Objects::nonNull) //
                    .distinct().collect(Collectors.toList());

            Map<String, User> userMap = users.parallelStream() //
                    .collect(Collectors.toMap(Function.identity(), //
                            (i) -> getUserWithCache(new Identify(service, i))));

            Context context = new Context();
            context.setAncestors(new ArrayList<>());
            context.setDescendants(new ArrayList<>());

            boolean isProceededMine = false;
            for (Message m : response.getMessages()) {
                if (m.getTs().equals(id.getId())) {
                    isProceededMine = true;
                    continue;
                }

                User user = userMap.get(m.getUser());
                Comment comment = SlackMapper.comment(m, user, userMe, candidates, getChannelId(id), service);
                ((!isProceededMine) ? context.getAncestors() : context.getDescendants()).add(comment);
            }

            List<Comment> comments = new ArrayList<>();
            comments.addAll(context.getAncestors());
            comments.addAll(context.getDescendants());
            setMentionName(comments, service);

            MapperUtil.sortContext(context);
            return context;
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
        return this.getTimeLine(getGeneralChannel(), paging);
    }

    /**
     * Get Timeline Comments
     * タイムライン情報を取得
     */
    private Pageable<Comment> getTimeLine(String channel, Paging paging) {
        return proceed(() -> {
            ExecutorService pool = Executors.newCachedThreadPool();

            // ------------------------------------------------ //
            // Async Request
            // ------------------------------------------------ //

            Future<ChannelsHistoryResponse> responseFuture = pool.submit(() -> {
                ChannelsHistoryRequestBuilder request = ChannelsHistoryRequest.builder() //
                        .channel(channel).token(auth.getAccessor().getToken());

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
            Future<User> userMeFuture = pool.submit(this::getUserMeWithCache);

            // ------------------------------------------------ //
            // Await Request
            // ------------------------------------------------ //

            ChannelsHistoryResponse response = responseFuture.get();
            List<ReactionCandidate> candidates = candidatesFuture.get();
            User userMe = userMeFuture.get();
            teamFuture.get();

            // ------------------------------------------------ //
            // Get User Instances
            // ------------------------------------------------ //

            Service service = getAccount().getService();

            // USERS
            List<String> users = response.getMessages().stream() //
                    .filter((e) -> !BotMessage.getCode().equals(e.getSubtype())) //
                    .map(Message::getUser).filter(Objects::nonNull) //
                    .distinct().collect(Collectors.toList());
            Map<String, User> userMap = users.parallelStream() //
                    .collect(Collectors.toMap(Function.identity(), //
                            (id) -> getUserWithCache(new Identify(service, id))));

            // BOTS
            List<String> bots = response.getMessages().stream() //
                    .filter((e) -> BotMessage.getCode().equals(e.getSubtype())) //
                    .map(Message::getBotId).filter(Objects::nonNull) //
                    .distinct().collect(Collectors.toList());
            Map<String, User> botMap = bots.parallelStream() //
                    .collect(Collectors.toMap(Function.identity(), //
                            (id) -> getBotWithCache(new Identify(service, id))));

            Pageable<Comment> pageable = SlackMapper.timeLine(response, //
                    userMap, botMap, userMe, candidates, channel, service, paging);

            // スレッド対象外 or スレッド元のみ表示対象
            pageable.setPredicate((comment) -> {
                String threadId = ((SlackComment) comment).getThreadId();
                return (threadId == null) || comment.getId().equals(threadId);
            });

            setMentionName(pageable.getEntities(), service);
            return pageable;
        });
    }

    // ============================================================== //
    // Channels
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Channel> getChannels(Identify id, Paging paging) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getChannelTimeLine(Identify id, Paging paging) {
        return this.getTimeLine((String) id.getId(), paging);
    }

    // ============================================================== //
    // Message
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Thread> getMessageThread(Paging paging) {
        return proceed(() -> {
            ExecutorService pool = Executors.newCachedThreadPool();

            Service service = getAccount().getService();
            Integer count = getCountFromPage(paging, 200);
            String cursor = getCursorFromPage(paging, null);
            String token = auth.getAccessor().getToken();

            Future<User> userFuture = pool.submit(this::getUserMeWithCache);

            Future<ImListResponse> imResponseFuture = pool.submit(() ->
                    auth.getAccessor().getSlack().methods()
                            .imList(ImListRequest.builder().token(token)
                                    .setGetLatest(true)
                                    .cursor(cursor)
                                    .limit(count)
                                    .build()));

            Future<MpimListResponse> mpimResponseFuture = pool.submit(() ->
                    auth.getAccessor().getSlack().methods()
                            .mpimList(MpimListRequest.builder().token(token)
                                    .setGetLatest(true)
                                    .cursor(cursor)
                                    .limit(count)
                                    .build()));

            // ユーザー ID を取得
            Set<String> userIds = new HashSet<>();
            ImListResponse imResponse = imResponseFuture.get();
            imResponse.getIms().forEach((im) -> userIds.add(im.getUser()));
            MpimListResponse mpimResponse = mpimResponseFuture.get();
            mpimResponse.getGroups().forEach((mpim) -> userIds.addAll(mpim.getMembers()));

            // アカウント & BOT の内容を取得
            User myAccount = userFuture.get();
            Map<String, User> accountMap = userIds.parallelStream() //
                    .collect(Collectors.toMap(Function.identity(), //
                            (id) -> getAccountWithCache(new Identify(service, id))));


            // スレッド一覧を取得
            List<Thread> threads = new ArrayList<>();
            threads.addAll(SlackMapper.thread(imResponse, accountMap, myAccount, service));
            threads.addAll(SlackMapper.thread(mpimResponse, accountMap, service));
            threads.sort(Comparator.comparing(Thread::getLastUpdate).reversed());

            Pageable<Thread> pageable = new Pageable<>();
            pageable.setEntities(threads);
            return pageable;
        });

    }

    // ============================================================== //
    // Request
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestAction request() {
        return new SlackRequest(getAccount());
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

    /**
     * Get Bots Information
     * ボットの情報を取得
     */
    public User getBots(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();
            BotsInfoResponse bots = auth.getAccessor().getSlack() //
                    .methods().botsInfo(BotsInfoRequest.builder() //
                            .token(auth.getAccessor().getToken()) //
                            .bot((String) id.getId()) //
                            .build());

            return botCache(SlackMapper.bots(bots, service));
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
            getChannels(null, null);
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

    /**
     * リプライのユーザー情報を埋める
     */
    private void setMentionName(List<Comment> comments, Service service) {

        // リプライされている ID 一覧を取得
        List<String> userIds = SlackMapper.getReplayUserIds(comments);

        // ユーザー一覧を取得
        Map<String, User> userMap = userIds.parallelStream() //
                .collect(Collectors.toMap(Function.identity(), //
                        (id) -> getUserWithCache(new Identify(service, id))));

        SlackMapper.setMentionName(comments, userMap);
    }

    // ============================================================== //
    // Cache
    // ============================================================== //

    private User userCache(User user) {
        user.getId(String.class).ifPresent( //
                (id) -> userCache.put(id, user));
        return user;
    }

    // Cache
    private User getUserWithCache(Identify id) {
        String time = id.getId(String.class).orElse(null);
        if (time != null && userCache.containsKey(time)) {
            return userCache.get(time);
        }
        return getUser(id);
    }

    private User botCache(User bot) {
        bot.getId(String.class).ifPresent( //
                (id) -> botCache.put(id, bot));
        return bot;
    }

    // Cache
    private User getBotWithCache(Identify id) {
        String time = id.getId(String.class).orElse(null);
        if (time != null && botCache.containsKey(time)) {
            return botCache.get(time);
        }
        return getBots(id);
    }

    private User getAccountWithCache(Identify id) {
        if (((String) id.getId()).startsWith("U")) {
            return getUserWithCache(id);
        }
        if (((String) id.getId()).startsWith("B")) {
            return getUserWithCache(id);
        }
        throw new NotImplimentedException();
    }

    // ============================================================== //
    // Paging
    // ============================================================== //

    private Integer getCountFromPage(Paging paging, Integer defValue) {
        if (paging != null) {
            if (paging.getCount() != null) {
                return paging.getCount().intValue();
            }
        }
        return defValue;
    }

    @SuppressWarnings("unchecked")
    private <T> T getCursorFromPage(Paging paging, T defValue) {
        if (paging != null) {
            if (paging instanceof CursorPaging) {
                CursorPaging pg = ((CursorPaging) paging);
                return (T) pg.getCurrentCursor();
            }
        }
        return defValue;
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
