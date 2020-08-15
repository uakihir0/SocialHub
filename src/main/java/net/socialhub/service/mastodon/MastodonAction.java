package net.socialhub.service.mastodon;

import mastodon4j.Mastodon;
import mastodon4j.MastodonException;
import mastodon4j.Page;
import mastodon4j.Range;
import mastodon4j.entity.Alert;
import mastodon4j.entity.Attachment;
import mastodon4j.entity.Conversation;
import mastodon4j.entity.History;
import mastodon4j.entity.Results;
import mastodon4j.entity.Status;
import mastodon4j.entity.request.StatusUpdate;
import mastodon4j.entity.share.Response;
import mastodon4j.streaming.LifeCycleListener;
import mastodon4j.streaming.PublicStream;
import mastodon4j.streaming.PublicStreamListener;
import mastodon4j.streaming.UserStream;
import mastodon4j.streaming.UserStreamListener;
import net.socialhub.define.action.service.MicroBlogActionType;
import net.socialhub.define.service.mastodon.MastodonNotificationType;
import net.socialhub.define.service.mastodon.MastodonReactionType;
import net.socialhub.define.service.mastodon.MastodonVisibility;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.error.NotSupportedException;
import net.socialhub.model.error.SocialHubException;
import net.socialhub.model.request.CommentForm;
import net.socialhub.model.request.PollForm;
import net.socialhub.model.service.Channel;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Context;
import net.socialhub.model.service.Error;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Notification;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Relationship;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.Thread;
import net.socialhub.model.service.Trend;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.mastodon.MastodonStream;
import net.socialhub.model.service.addition.mastodon.MastodonThread;
import net.socialhub.model.service.event.CommentEvent;
import net.socialhub.model.service.event.IdentifyEvent;
import net.socialhub.model.service.event.NotificationEvent;
import net.socialhub.model.service.event.UserEvent;
import net.socialhub.model.service.paging.BorderPaging;
import net.socialhub.model.service.paging.OffsetPaging;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;
import net.socialhub.service.action.RequestAction;
import net.socialhub.service.action.callback.EventCallback;
import net.socialhub.service.action.callback.comment.DeleteCommentCallback;
import net.socialhub.service.action.callback.comment.MentionCommentCallback;
import net.socialhub.service.action.callback.comment.NotificationCommentCallback;
import net.socialhub.service.action.callback.comment.UpdateCommentCallback;
import net.socialhub.service.action.callback.lifecycle.ConnectCallback;
import net.socialhub.service.action.callback.lifecycle.DisconnectCallback;
import net.socialhub.service.action.callback.user.FollowUserCallback;
import net.socialhub.service.action.specific.MicroBlogAccountAction;
import net.socialhub.utils.MapperUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static net.socialhub.define.action.OtherActionType.BlockUser;
import static net.socialhub.define.action.OtherActionType.DeleteComment;
import static net.socialhub.define.action.OtherActionType.FollowUser;
import static net.socialhub.define.action.OtherActionType.GetChannels;
import static net.socialhub.define.action.OtherActionType.GetComment;
import static net.socialhub.define.action.OtherActionType.GetContext;
import static net.socialhub.define.action.OtherActionType.GetRelationship;
import static net.socialhub.define.action.OtherActionType.GetUser;
import static net.socialhub.define.action.OtherActionType.GetUserMe;
import static net.socialhub.define.action.OtherActionType.LikeComment;
import static net.socialhub.define.action.OtherActionType.MuteUser;
import static net.socialhub.define.action.OtherActionType.PostComment;
import static net.socialhub.define.action.OtherActionType.ShareComment;
import static net.socialhub.define.action.OtherActionType.UnShareComment;
import static net.socialhub.define.action.OtherActionType.UnblockUser;
import static net.socialhub.define.action.OtherActionType.UnfollowUser;
import static net.socialhub.define.action.OtherActionType.UnlikeComment;
import static net.socialhub.define.action.OtherActionType.UnmuteUser;
import static net.socialhub.define.action.TimeLineActionType.ChannelTimeLine;
import static net.socialhub.define.action.TimeLineActionType.HomeTimeLine;
import static net.socialhub.define.action.TimeLineActionType.MentionTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserCommentTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserLikeTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserMediaTimeLine;
import static net.socialhub.define.action.UsersActionType.GetFollowerUsers;
import static net.socialhub.define.action.UsersActionType.GetFollowingUsers;
import static net.socialhub.define.action.UsersActionType.SearchUsers;

public class MastodonAction extends AccountActionImpl implements MicroBlogAccountAction {

    private static final Logger logger = Logger.getLogger(MastodonAction.class);

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
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<mastodon4j.entity.Account> account = mastodon.verifyCredentials();

            service.getRateLimit().addInfo(GetUserMe, account);
            me = MastodonMapper.user(account.get(), service);
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
     * https://mastodon.social/@uakihir0
     * https://mastodon.social/web/accounts/1223371
     */
    @Override
    public User getUser(String url) {
        return proceed(() -> {
            {
                Pattern regex = Pattern.compile("https://(.+?)/@(.+)");
                Matcher matcher = regex.matcher(url);

                if (matcher.matches()) {
                    String host = matcher.group(1);
                    String sname = matcher.group(2);
                    String format = ("@" + sname + "@" + host);
                    Pageable<User> users = searchUsers(format, null);
                    Optional<User> user = users.getEntities().stream()
                            .filter(u -> u.getAccountIdentify().equals(format))
                            .findFirst();

                    if (user.isPresent()) {
                        return user.get();
                    }
                }
            }
            {
                Service service = getAccount().getService();
                Pattern regex = Pattern.compile("https://(.+?)/web/accounts/(.+)");
                Matcher matcher = regex.matcher(url);

                if (matcher.matches()) {
                    Long id = Long.parseLong(matcher.group(2));
                    Identify identify = new Identify(service, id);
                    return getUser(identify);
                }
            }
            throw new SocialHubException("this url is not supported format.");
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
            Response<?> relationship = mastodon.follow((Long) id.getId());

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
            Response<?> relationship = mastodon.unfollow((Long) id.getId());

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
            Response<?> relationship = mastodon.mute((Long) id.getId());

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
            Response<?> relationship = mastodon.unmute((Long) id.getId());

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
            Response<?> relationship = mastodon.block((Long) id.getId());

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
            Response<?> relationship = mastodon.unblock((Long) id.getId());

            service.getRateLimit().addInfo(UnblockUser, relationship);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship getRelationship(Identify id) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<mastodon4j.entity.Relationship[]> relationships = //
                    mastodon.relationships((Long) id.getId());

            service.getRateLimit().addInfo(GetRelationship, relationships);
            return MastodonMapper.relationship(relationships.get()[0]);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> searchUsers(String query, Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Page page = getPage(paging);

            Response<Results> results = mastodon.search().search( //
                    query, false, false, page);

            service.getRateLimit().addInfo(SearchUsers, results);
            return MastodonMapper.users(results.get().getAccounts(), service, paging);
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
    @Override
    public Pageable<Comment> getMentionTimeLine(Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Range range = getRange(paging);

            Response<mastodon4j.entity.Notification[]> status = //
                    mastodon.notifications() //
                            .getNotifications(range, Arrays.asList( //
                                    MastodonNotificationType.FOLLOW.getCode(), //
                                    MastodonNotificationType.FAVOURITE.getCode(), //
                                    MastodonNotificationType.REBLOG.getCode()), //
                                    null);

            List<Status> statuses = Stream.of(status.get()) //
                    .map(mastodon4j.entity.Notification::getStatus)
                    .collect(toList());

            service.getRateLimit().addInfo(MentionTimeLine, status);
            return MastodonMapper.timeLine(statuses, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
                if (id.getId().equals(getUserMeWithCache().getId())) {

                    Mastodon mastodon = auth.getAccessor();
                    Service service = getAccount().getService();
                    Range range = getRange(paging);

                    Response<Status[]> status = mastodon.favourites().getFavourites(range);

                    service.getRateLimit().addInfo(UserLikeTimeLine, status);
                    return MastodonMapper.timeLine(status.get(), service, paging);
                }
            }

            throw new NotSupportedException( //
                    "Sorry, user favorites timeline is only support only verified account on Mastodon.");
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getSearchTimeLine(String query, Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();

            if (query.startsWith("#")) {

                // ハッシュタグのクエリの場合
                Range range = getRange(paging);
                Response<Status[]> results = mastodon.getHashtagTimeline( //
                        query.substring(1), false, false, range);

                return MastodonMapper.timeLine(results.get(), service, paging);

            } else {

                // それ以外は通常の検索を実施
                Page page = getPage(paging);
                Response<Results> results = mastodon.search().search( //
                        query, false, false, page);

                return MastodonMapper.timeLine(results.get().getStatuses(), service, paging);
            }
        });
    }

    // ============================================================== //
    // Comment
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void postComment(CommentForm req) {
        proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();

            StatusUpdate update = new StatusUpdate();
            update.setContent(req.getText());

            // 返信の処理
            if (req.getTargetId() != null) {
                update.setInReplyToId((Long) req.getTargetId());
            }

            // ダイレクトメッセージの場合
            if (req.isMessage()) {
                update.setVisibility(MastodonVisibility.Direct.getCode());
            }

            // 画像の処理
            if (req.getImages() != null && !req.getImages().isEmpty()) {
                update.setMediaIds(new ArrayList<>());

                // Mastodon はアップロードされた順番で配置が決定
                // -> 並列にメディアをアップロードせずに逐次上げる
                req.getImages().forEach(image -> {
                    InputStream input = new ByteArrayInputStream(image.getData());
                    Response<Attachment> attachment = mastodon.media() //
                            .postMedia(input, image.getName(), null);
                    update.getMediaIds().add(attachment.get().getId());
                });
            }

            // センシティブな内容
            if (req.isSensitive()) {
                update.setSensitive(true);
            }

            // 投票
            if (req.getPoll() != null) {
                PollForm poll = req.getPoll();
                update.setPollOptions(poll.getOptions());
                update.setPollMultiple(poll.getMultiple());
                update.setPollExpiresIn(poll.getExpiresIn() * 60);
            }

            Response<Status> status = mastodon.statuses().postStatus(update);
            service.getRateLimit().addInfo(PostComment, status);
        });
    }

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
     * https://mastodon.social/web/statuses/104681506368424218
     */
    @Override
    public Comment getComment(String url) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Pattern regex = Pattern.compile("https://(.+?)/web/statuses/(.+)");
            Matcher matcher = regex.matcher(url);

            if (matcher.matches()) {
                Long id = Long.parseLong(matcher.group(2));
                Identify identify = new Identify(service, id);
                return getComment(identify);
            }
            throw new SocialHubException("this url is not supported format.");
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void likeComment(Identify id) {
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
    public void unlikeComment(Identify id) {
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
    public void shareComment(Identify id) {
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
    public void unshareComment(Identify id) {
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
    public void reactionComment(Identify id, String reaction) {
        if (reaction != null && !reaction.isEmpty()) {
            String type = reaction.toLowerCase();

            if (MastodonReactionType.Favorite.getCode().contains(type)) {
                likeComment(id);
                return;
            }
            if (MastodonReactionType.Reblog.getCode().contains(type)) {
                retweetComment(id);
                return;
            }
        }
        throw new NotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unreactionComment(Identify id, String reaction) {
        if (reaction != null && !reaction.isEmpty()) {
            String type = reaction.toLowerCase();

            if (MastodonReactionType.Favorite.getCode().contains(type)) {
                unlikeComment(id);
                return;
            }
            if (MastodonReactionType.Reblog.getCode().contains(type)) {
                unretweetComment(id);
                return;
            }
        }
        throw new NotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteComment(Identify id) {
        proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Response<Void> voids = mastodon.statuses().deleteStatus((Long) id.getId());

            service.getRateLimit().addInfo(DeleteComment, voids);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReactionCandidate> getReactionCandidates() {
        return MastodonMapper.reactionCandidates();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getCommentContext(Identify id) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();

            Long displayId = (Long) ((id instanceof Comment) ? //
                    ((Comment) id).getDisplayComment().getId() : id.getId());

            Response<mastodon4j.entity.Context> response = mastodon.getContext(displayId);

            service.getRateLimit().addInfo(GetContext, response);

            Context context = new Context();
            context.setDescendants(Arrays.stream(response.get().getDescendants()) //
                    .map(e -> MastodonMapper.comment(e, service)) //
                    .collect(toList()));
            context.setAncestors(Arrays.stream(response.get().getAncestors()) //
                    .map(e -> MastodonMapper.comment(e, service)) //
                    .collect(toList()));

            MapperUtil.sortContext(context);
            return context;
        });
    }

    // ============================================================== //
    // Channel (List) API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Channel> getChannels(Identify id, Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();

            if (id != null) {
                User me = getUserMeWithCache();
                if (!me.getId().equals(id.getId())) {
                    throw new NotSupportedException(
                            "Sorry, authenticated user only.");
                }
            }

            Response<mastodon4j.entity.List[]> lists = mastodon.list().getLists();
            service.getRateLimit().addInfo(GetChannels, lists);
            return MastodonMapper.channels(lists.get(), service);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getChannelTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Range range = getRange(paging);

            Response<Status[]> status = mastodon.timelines()
                    .getListTimeline((String) id.getId(), range);

            service.getRateLimit().addInfo(ChannelTimeLine, status);
            return MastodonMapper.timeLine(status.get(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getChannelUsers(Identify id, Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Long limit = (paging != null) ? paging.getCount() : null;

            Response<mastodon4j.entity.Account[]> status = mastodon
                    .list().getListAccounts((String) id.getId(), limit);

            service.getRateLimit().addInfo(ChannelTimeLine, status);
            return MastodonMapper.users(status.get(), service, paging);
        });
    }

    // ============================================================== //
    // Message API
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Thread> getMessageThread(Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Range range = getRange(paging);

            Response<Conversation[]> response =
                    mastodon.timelines().getConversations(range);

            List<Thread> threads = new ArrayList<>();
            for (Conversation conv : response.get()) {

                // 最後のコメントを取得
                Comment comment = MastodonMapper
                        .comment(conv.getLastStatus(), service);

                // 「名前: コメント内容」のフォーマットで説明文を作成
                String description = comment.getUser().getName()
                        + ": " + comment.getText().getDisplayText();

                MastodonThread thread = new MastodonThread(service);
                thread.setLastUpdate(comment.getCreateAt());
                thread.setDescription(description);
                thread.setUsers(new ArrayList<>());
                thread.setLastComment(comment);
                thread.setId(conv.getId());
                threads.add(thread);

                // アカウントリストを設定
                for (mastodon4j.entity.Account account : conv.getAccounts()) {
                    User user = MastodonMapper.user(account, service);
                    thread.getUsers().add(user);
                }
            }

            BorderPaging border = BorderPaging.fromPaging(paging);
            MastodonMapper.beMastodonPaging(border);

            Pageable<Thread> results = new Pageable<>();
            results.setEntities(threads);
            results.setPaging(border);
            return results;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getMessageTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Long commentId = null;
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();

            // Identify を直接作成した場合
            if (id.getId() instanceof Long) {
                commentId = (Long) id.getId();
            }
            // MastodonThread から呼び出した場合
            if (id instanceof MastodonThread) {
                MastodonThread th = (MastodonThread) id;
                commentId = (Long) (th.getLastComment().getId());
            }

            // ID が発見できない場合
            if (commentId == null) {
                String message = "matched id is not found.";
                throw new IllegalStateException(message);
            }

            Response<mastodon4j.entity.Context> response =
                    mastodon.getContext(commentId);

            List<Comment> comments = new ArrayList<>();
            comments.addAll(Arrays.stream(response.get().getDescendants()) //
                    .map(e -> MastodonMapper.comment(e, service)) //
                    .collect(toList()));
            comments.addAll(Arrays.stream(response.get().getAncestors()) //
                    .map(e -> MastodonMapper.comment(e, service)) //
                    .collect(toList()));

            // 最後のコメントも追加
            if (id instanceof MastodonThread) {
                comments.add(((MastodonThread) id).getLastComment());
            }

            comments.sort(comparing(Comment::getCreateAt).reversed());
            service.getRateLimit().addInfo(GetContext, response);

            Pageable<Comment> pageable = new Pageable<>();
            pageable.setEntities(comments);
            pageable.setPaging(new Paging());
            pageable.getPaging().setCount(0L);
            pageable.getPaging().setHasNext(false);
            pageable.getPaging().setHasPast(false);

            return pageable;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postMessage(CommentForm req) {
        postComment(req);
    }

    // ============================================================== //
    // Stream
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public net.socialhub.model.service.Stream
    setHomeTimeLineStream(EventCallback callback) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();

            MastodonStream model = new MastodonStream();
            UserStream stream = mastodon.streaming().userStream().register(
                    new MastodonCommentListener(callback, service),
                    new MastodonConnectionListener(callback, model));

            model.setStream(stream);
            return model;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public net.socialhub.model.service.Stream
    setNotificationStream(EventCallback callback) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();

            MastodonStream model = new MastodonStream();
            UserStream stream = mastodon.streaming().userStream().register(
                    new MastodonNotificationListener(callback, service),
                    new MastodonConnectionListener(callback, model));

            model.setStream(stream);
            return model;
        });
    }

    // ============================================================== //
    // Poll
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public void votePoll(Identify id, List<Integer> choices) {
        proceed(() -> {
            Mastodon mastodon = auth.getAccessor();

            long[] array = choices.stream().mapToLong(e -> e).toArray();
            mastodon.votePoll((Long) id.getId(), array);
        });
    }

    // ============================================================== //
    // Other
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Trend> getTrends(Integer limit) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Response<mastodon4j.entity.Trend[]> response =
                    mastodon.trend().getTrends(limit.longValue());

            List<Trend> results = new ArrayList<>();
            for (mastodon4j.entity.Trend trend : response.get()) {

                Trend model = new Trend();
                model.setName("#" + trend.getName());
                model.setQuery("#" + trend.getName());

                int uses = Arrays
                        .stream(trend.getHistory())
                        .map(History::getUses)
                        .reduce(Long::sum)
                        .orElse(0L)
                        .intValue();

                model.setVolume(uses);
                results.add(model);
            }

            return results;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Notification> getNotification(Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Range range = getRange(paging);

            Response<mastodon4j.entity.Notification[]> notifications =
                    mastodon.notifications()
                            .getNotifications(range, Arrays.asList(
                                    MastodonNotificationType.MENTION.getCode(),
                                    MastodonNotificationType.POLL.getCode()),
                                    null);

            return MastodonMapper.notifications(
                    notifications.get(), service, paging);
        });
    }

    /**
     * Get Notification (Single)
     * 通知情報を取得
     */
    public Notification getNotification(Identify identify) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();

            Response<mastodon4j.entity.Notification> notification =
                    mastodon.notifications()
                            .getNotification((Long) identify.getId());

            return MastodonMapper.notification(
                    notification.get(), service);
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
        return new MastodonRequest(getAccount());
    }

    // ============================================================== //
    // Only Mastodon
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getLocalTimeLine(Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Range range = getRange(paging);

            Response<Status[]> status = mastodon.getPublicTimeline(true, false, range);
            service.getRateLimit().addInfo(MicroBlogActionType.LocalTimeLine, status);
            return MastodonMapper.timeLine(status.get(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getFederationTimeLine(Paging paging) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Range range = getRange(paging);

            Response<Status[]> status = mastodon.getPublicTimeline(false, false, range);
            service.getRateLimit().addInfo(MicroBlogActionType.FederationTimeLine, status);
            return MastodonMapper.timeLine(status.get(), service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public net.socialhub.model.service.Stream
    setLocalLineStream(EventCallback callback) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();

            MastodonStream model = new MastodonStream();
            PublicStream stream = mastodon.streaming().publicStream(true).register(
                    new MastodonCommentListener(callback, service),
                    new MastodonConnectionListener(callback, model));

            model.setStream(stream);
            return model;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public net.socialhub.model.service.Stream
    setFederationLineStream(EventCallback callback) {
        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();

            MastodonStream model = new MastodonStream();
            PublicStream stream = mastodon.streaming().publicStream(false).register(
                    new MastodonCommentListener(callback, service),
                    new MastodonConnectionListener(callback, model));

            model.setStream(stream);
            return model;
        });
    }

    /**
     * Register ServiceWorker endpoint.
     * サービスワーカーのエンドポイントを設定
     */
    public void registerSubscription(
            String endpoint, String publicKey, String authSecret) {

        proceed(() -> {
            Mastodon mastodon = auth.getAccessor();

            // All notification
            Alert alert = new Alert();
            alert.setFollow(true);
            alert.setFavourite(true);
            alert.setReblog(true);
            alert.setMention(true);
            alert.setPoll(true);

            mastodon.notifications().pushSubscription(
                    endpoint, publicKey, authSecret, alert);
        });
    }

    /**
     * Get user pinned comments.
     * ユーザーのピンされたコメントを取得
     */
    public List<Comment> getUserPinedComments(Identify id) {

        return proceed(() -> {
            Mastodon mastodon = auth.getAccessor();
            Service service = getAccount().getService();
            Range range = new Range();
            range.setLimit(100);

            Response<Status[]> status = mastodon.accounts().getStatuses( //
                    (Long) id.getId(), true, false, false, false, range);

            return Stream.of(status.get())
                    .map(s -> MastodonMapper.comment(s, service))
                    .collect(toList());
        });
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

    private static Page getPage(Paging paging) {
        if (paging == null) {
            return null;
        }
        Page pg = new Page();
        pg.setLimit(paging.getCount());

        if (paging instanceof OffsetPaging) {
            OffsetPaging offset = (OffsetPaging) paging;

            if (offset.getOffset() != null) {
                pg.setOffset(offset.getOffset());
            }
        }
        return pg;
    }

    // ============================================================== //
    // Classes
    // ============================================================== //

    // コメントに対してのコールバック設定
    static class MastodonCommentListener implements
            UserStreamListener,
            PublicStreamListener {

        private EventCallback listener;
        private Service service;

        MastodonCommentListener(
                EventCallback listener,
                Service service) {
            this.listener = listener;
            this.service = service;
        }

        @Override
        public void onUpdate(Status status) {
            if (listener instanceof UpdateCommentCallback) {
                Comment comment = MastodonMapper.comment(status, service);
                CommentEvent event = new CommentEvent(comment);
                ((UpdateCommentCallback) listener).onUpdate(event);
            }
        }

        @Override
        public void onDelete(long id) {
            if (listener instanceof DeleteCommentCallback) {
                IdentifyEvent event = new IdentifyEvent(id);
                ((DeleteCommentCallback) listener).onDelete(event);
            }
        }
    }

    // 通信に対してのコールバック設定
    static class MastodonConnectionListener implements LifeCycleListener {

        private final EventCallback listener;
        private final MastodonStream stream;

        MastodonConnectionListener(
                EventCallback listener,
                MastodonStream stream) {
            this.listener = listener;
            this.stream = stream;
        }

        @Override
        public void onConnect() {
            stream.setConnecting(true);
            if (listener instanceof ConnectCallback) {
                ((ConnectCallback) listener).onConnect();
            }
        }

        @Override
        public void onDisconnect() {
            stream.setConnecting(false);
            if (listener instanceof DisconnectCallback) {
                ((DisconnectCallback) listener).onDisconnect();
            }
        }
    }

    // 通知に対してのコールバック設定
    static class MastodonNotificationListener implements
            UserStreamListener,
            PublicStreamListener {

        private final EventCallback listener;
        private final Service service;

        MastodonNotificationListener(
                EventCallback listener,
                Service service) {
            this.listener = listener;
            this.service = service;
        }

        @Override
        public void onNotification(mastodon4j.entity.Notification notification) {

            MastodonNotificationType type =
                    MastodonNotificationType.of(notification.getType());

            if (type == MastodonNotificationType.MENTION ||
                    type == MastodonNotificationType.FOLLOW ||
                    type == MastodonNotificationType.REBLOG ||
                    type == MastodonNotificationType.FAVOURITE) {

                // Mention の場合は先に処理
                if (type == MastodonNotificationType.MENTION) {
                    if (listener instanceof MentionCommentCallback) {
                        Comment model = MastodonMapper.comment(notification.getStatus(), service);
                        ((MentionCommentCallback) listener).onMention(new CommentEvent(model));
                    }
                    return;
                }

                Notification model = MastodonMapper.notification(notification, service);

                switch (type) {
                    case FOLLOW:
                        if (listener instanceof FollowUserCallback) {
                            ((FollowUserCallback) listener).onFollow(
                                    new UserEvent(model.getUsers().get(0)));
                        }
                        return;

                    case REBLOG:
                    case FAVOURITE:
                        if (listener instanceof NotificationCommentCallback) {
                            ((NotificationCommentCallback) listener).onNotification(
                                    new NotificationEvent(model));
                        }
                        return;

                    default:
                        throw new IllegalStateException();
                }
            }
        }
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
        SocialHubException se = new SocialHubException(e);

        if (e instanceof MastodonException) {
            MastodonException me = (MastodonException) e;

            // エラーメッセージが設定されているエラーである場合
            if (me.getError() != null && me.getError().getDescription() != null) {
                String description = me.getError().getDescription();
                se.setError(new Error(description));
            }
        }

        throw se;
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
