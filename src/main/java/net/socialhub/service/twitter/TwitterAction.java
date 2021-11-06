package net.socialhub.service.twitter;

import net.socialhub.define.ErrorType;
import net.socialhub.define.MediaType;
import net.socialhub.define.NotificationActionType;
import net.socialhub.define.service.twitter.TwitterReactionType;
import net.socialhub.define.service.twitter.TwitterSearchBuilder;
import net.socialhub.define.service.twitter.TwitterSearchQuery;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.error.NotSupportedException;
import net.socialhub.model.error.SocialHubException;
import net.socialhub.model.request.CommentForm;
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
import net.socialhub.model.service.addition.twitter.TwitterComment;
import net.socialhub.model.service.addition.twitter.TwitterThread;
import net.socialhub.model.service.addition.twitter.TwitterUser;
import net.socialhub.model.service.event.CommentEvent;
import net.socialhub.model.service.event.IdentifyEvent;
import net.socialhub.model.service.event.NotificationEvent;
import net.socialhub.model.service.paging.CursorPaging;
import net.socialhub.model.service.paging.IndexPaging;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.model.service.support.TrendComment;
import net.socialhub.model.service.support.TrendCountry;
import net.socialhub.model.service.support.TrendCountry.TrendLocation;
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
import net.socialhub.twitter.web.TwitterWebClient;
import net.socialhub.twitter.web.entity.request.SpecifiedTweetRequest;
import net.socialhub.twitter.web.entity.request.UserTimelineRequest;
import net.socialhub.twitter.web.entity.request.graphql.ScreenNameRequest;
import net.socialhub.twitter.web.entity.response.TopLevel;
import net.socialhub.twitter.web.entity.response.graphql.GraphRoot;
import net.socialhub.utils.HandlingUtil;
import net.socialhub.utils.MapperUtil;
import net.socialhub.utils.SnowflakeUtil;
import twitter4j.ConnectionLifeCycleListener;
import twitter4j.DirectMessage;
import twitter4j.DirectMessageList;
import twitter4j.FilterQuery;
import twitter4j.IDs;
import twitter4j.Location;
import twitter4j.PagableResponseList;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.SavedSearch;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusUpdate;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.UserList;
import work.socialhub.JTW;
import work.socialhub.api.JTWFactory;
import work.socialhub.api.request.UsersLookupIdRequest;
import work.socialhub.api.response.Response;
import work.socialhub.api.response.Root;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static net.socialhub.define.action.OtherActionType.BlockUser;
import static net.socialhub.define.action.OtherActionType.DeleteComment;
import static net.socialhub.define.action.OtherActionType.FollowUser;
import static net.socialhub.define.action.OtherActionType.GetChannels;
import static net.socialhub.define.action.OtherActionType.GetComment;
import static net.socialhub.define.action.OtherActionType.GetRelationship;
import static net.socialhub.define.action.OtherActionType.GetUser;
import static net.socialhub.define.action.OtherActionType.GetUserMe;
import static net.socialhub.define.action.OtherActionType.LikeComment;
import static net.socialhub.define.action.OtherActionType.MuteUser;
import static net.socialhub.define.action.OtherActionType.ShareComment;
import static net.socialhub.define.action.OtherActionType.UnShareComment;
import static net.socialhub.define.action.OtherActionType.UnblockUser;
import static net.socialhub.define.action.OtherActionType.UnfollowUser;
import static net.socialhub.define.action.OtherActionType.UnlikeComment;
import static net.socialhub.define.action.OtherActionType.UnmuteUser;
import static net.socialhub.define.action.TimeLineActionType.ChannelTimeLine;
import static net.socialhub.define.action.TimeLineActionType.HomeTimeLine;
import static net.socialhub.define.action.TimeLineActionType.MentionTimeLine;
import static net.socialhub.define.action.TimeLineActionType.SearchTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserCommentTimeLine;
import static net.socialhub.define.action.TimeLineActionType.UserLikeTimeLine;
import static net.socialhub.define.action.UsersActionType.ChannelUsers;
import static net.socialhub.define.action.UsersActionType.GetFollowerUsers;
import static net.socialhub.define.action.UsersActionType.GetFollowingUsers;
import static net.socialhub.define.action.UsersActionType.SearchUsers;
import static net.socialhub.utils.CollectionUtil.partitionList;

/**
 * Twitter Actions
 * (All Actions)
 */
public class TwitterAction extends AccountActionImpl {

    private static final Logger logger = Logger.getLogger(TwitterAction.class);

    private ServiceAuth<Twitter> auth;

    // -------------------------------------------------------------- //
    // Twitter WebClient Fields
    // -------------------------------------------------------------- //

    private boolean useWebClient = true;

    private TwitterWebClient webClient = null;

    private JTW v2Client = null;

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
            twitter4j.User user = auth.getAccessor().verifyCredentials();
            service.getRateLimit().addInfo(GetUserMe, user);

            me = TwitterMapper.user(user, service);
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

            // ID
            if (id.getId(Long.class).isPresent()) {
                twitter4j.User user = auth.getAccessor().showUser((Long) id.getId());
                service.getRateLimit().addInfo(GetUser, user);
                return TwitterMapper.user(user, service);
            }

            // Screen Name
            if (id.getId(String.class).isPresent()) {
                twitter4j.User user = auth.getAccessor().showUser((String) id.getId());
                service.getRateLimit().addInfo(GetUser, user);
                return TwitterMapper.user(user, service);
            }

            return null;
        });
    }

    /**
     * {@inheritDoc}
     * Parse Twitter user's url, like:
     * https://twitter.com/uakihir0
     */
    @Override
    public User getUser(String url) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Pattern regex = Pattern.compile("https://twitter.com/(.+?)");
            Matcher matcher = regex.matcher(url);

            if (matcher.matches()) {
                String sname = matcher.group(1);
                twitter4j.User user = auth.getAccessor().showUser(sname);
                service.getRateLimit().addInfo(GetUser, user);
                return TwitterMapper.user(user, service);
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
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().createFriendship((Long) id.getId());
            service.getRateLimit().addInfo(FollowUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unfollowUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().destroyFriendship((Long) id.getId());
            service.getRateLimit().addInfo(UnfollowUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void muteUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().createMute((Long) id.getId());
            service.getRateLimit().addInfo(MuteUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmuteUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().destroyMute((Long) id.getId());
            service.getRateLimit().addInfo(UnmuteUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void blockUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().createBlock((Long) id.getId());
            service.getRateLimit().addInfo(BlockUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unblockUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getAccessor().destroyBlock((Long) id.getId());
            service.getRateLimit().addInfo(UnblockUser, after);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship getRelationship(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();

            User me = getUserMeWithCache();
            twitter4j.Relationship relationship = auth.getAccessor() //
                    .showFriendship((Long) me.getId(), (Long) id.getId());

            service.getRateLimit().addInfo(GetRelationship, relationship);
            return TwitterMapper.relationship(relationship);
        });
    }

    // ============================================================== //
    // Users
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getFollowingUsers(Identify id, Paging paging) {
        return proceed(() -> {
            int count = getCountFromPage(paging, 20);
            long cursor = getCursorFromPage(paging, -1L);

            Service service = getAccount().getService();
            PagableResponseList<twitter4j.User> users = auth.getAccessor() //
                    .getFriendsList((Long) id.getId(), cursor, count);

            service.getRateLimit().addInfo(GetFollowingUsers, users);
            return TwitterMapper.users(users, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getFollowerUsers(Identify id, Paging paging) {
        return proceed(() -> {
            int count = getCountFromPage(paging, 20);
            long cursor = getCursorFromPage(paging, -1L);

            Service service = getAccount().getService();
            PagableResponseList<twitter4j.User> users = auth.getAccessor() //
                    .getFollowersList((Long) id.getId(), cursor, count);

            service.getRateLimit().addInfo(GetFollowerUsers, users);
            return TwitterMapper.users(users, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> searchUsers(String query, Paging paging) {

        return proceed(() -> {
            Service service = getAccount().getService();

            int page = 1;
            if (paging != null) {
                paging.setCount(20L);

                if (paging instanceof IndexPaging) {
                    IndexPaging ind = (IndexPaging) paging;
                    if (ind.getPage() != null) {
                        page = ind.getPage().intValue();
                    }
                }
            }

            ResponseList<twitter4j.User> users = auth.getAccessor() //
                    .searchUsers(query, page);

            service.getRateLimit().addInfo(SearchUsers, users);
            return TwitterMapper.users(users, service, paging);
        });
    }

    // ============================================================== //
    // TimeLine
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getHomeTimeLine(Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();
            ResponseList<Status> statues = (paging == null) ? twitter.getHomeTimeline() //
                    : twitter.getHomeTimeline(TwitterMapper.fromPaging(paging));

            service.getRateLimit().addInfo(HomeTimeLine, statues);
            return TwitterMapper.timeLine(statues, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getMentionTimeLine(Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();
            ResponseList<Status> statues = (paging == null) ? twitter.getMentionsTimeline() //
                    : twitter.getMentionsTimeline(TwitterMapper.fromPaging(paging));

            service.getRateLimit().addInfo(MentionTimeLine, statues);
            return TwitterMapper.timeLine(statues, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserCommentTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            ResponseList<Status> statues = null;
            twitter4j.Paging page = (paging == null) ? //
                    null : TwitterMapper.fromPaging(paging);

            // ID
            if (id.getId(Long.class).isPresent()) {
                statues = (paging == null) ? //
                        twitter.getUserTimeline((Long) id.getId()) //
                        : twitter.getUserTimeline((Long) id.getId(), page);
            }
            // Screen Name
            if (id.getId(String.class).isPresent()) {
                statues = (paging == null) ? //
                        twitter.getUserTimeline((String) id.getId()) //
                        : twitter.getUserTimeline((String) id.getId(), page);
            }

            if (statues != null) {
                service.getRateLimit().addInfo(UserCommentTimeLine, statues);
                return TwitterMapper.timeLine(statues, service, paging);
            }
            throw new IllegalStateException();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserLikeTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            ResponseList<Status> statues = null;
            twitter4j.Paging page = (paging == null) ? //
                    null : TwitterMapper.fromPaging(paging);

            // ID
            if (id.getId(Long.class).isPresent()) {
                statues = (paging == null) ? //
                        twitter.getFavorites((Long) id.getId()) //
                        : twitter.getFavorites((Long) id.getId(), page);
            }
            // Screen Name
            if (id.getId(String.class).isPresent()) {
                statues = (paging == null) ? //
                        twitter.getFavorites((String) id.getId()) //
                        : twitter.getFavorites((String) id.getId(), page);
            }

            if (statues != null) {
                service.getRateLimit().addInfo(UserLikeTimeLine, statues);
                return TwitterMapper.timeLine(statues, service, paging);
            }
            throw new IllegalStateException();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserMediaTimeLine(Identify id, Paging paging) {
        return proceed(() -> {

            if (useWebClient) {

                // 公開アカウントの場合 Web API から取得
                if (id instanceof TwitterUser) {
                    TwitterUser twuser = (TwitterUser) id;
                    if (!twuser.getProtected()) {

                        try {
                            Twitter twitter = auth.getAccessor();
                            Service service = getAccount().getService();

                            UserTimelineRequest request = new UserTimelineRequest();
                            request.setUserId(twuser.getId().toString());
                            request.setCount(getCountFromPage(paging, 100, 100));
                            request.setCursor(getCursorFromPage(paging, null));

                            TopLevel top = getWebClient().timeline()
                                    .getUserMediaTimeline(request).get();

                            long[] tweetIds = top.toTweetTimeline().stream()
                                    .mapToLong(e -> Long.parseLong(e.getId()))
                                    .toArray();

                            CursorPaging<String> pg = CursorPaging.fromPaging(paging);
                            pg.setHasNext(top.getBottomCursor() != null);
                            pg.setNextCursor(top.getBottomCursor());
                            pg.setHasPrev(top.getTopCursor() != null);
                            pg.setPrevCursor(top.getTopCursor());

                            ResponseList<Status> statues = twitter.tweets().lookup(tweetIds);
                            Pageable<Comment> pageable = TwitterMapper.timeLine(statues, service, paging);
                            pageable.setPaging(pg);
                            return pageable;

                        } catch (Exception e) {

                            // Web 経由で取得できない場合は API 連続呼び出しを試行する
                            logger.debug("failed to get data from web api.", e);
                        }
                    }
                }
            }

            int requestCount = 0;
            int maxMediaCount = 50;
            if (paging != null && paging.getCount() != null) {
                maxMediaCount = paging.getCount().intValue();
            }

            // リクエスト時の PagingCount は 200 に固定
            // (メディアが存在しない場合もあるので多めに取得)
            Paging mediaPaging = (paging != null) ? paging : new Paging();
            mediaPaging.setCount(200L);

            Paging storedCursor = paging;
            Paging currentCursor = paging;
            List<Comment> comments = new ArrayList<>();

            // 順にリクエストする必要があるのでループを実行
            while ((requestCount < 10) && (comments.size() <= maxMediaCount)) {

                storedCursor = currentCursor;
                Pageable<Comment> results = getUserCommentTimeLine(id, currentCursor);
                currentCursor = results.pastPage();
                requestCount++;

                for (Comment comment : results.getEntities()) {
                    if (comment.getMedias().size() > 0) {
                        if (comment.getMedias().stream().anyMatch((e) -> //
                                (e.getType() == MediaType.Image) || //
                                        (e.getType() == MediaType.Movie))) {

                            // メディアコメントの場合
                            comments.add(comment);
                        }
                    }
                }
            }

            Pageable<Comment> pageable = new Pageable<>();
            pageable.setPaging(storedCursor);
            pageable.setEntities(comments);
            return pageable;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getSearchTimeLine(String query, Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            Query q = TwitterMapper.queryFromPaging(paging).query(query);
            QueryResult result = twitter.search(q);

            service.getRateLimit().addInfo(SearchTimeLine, result);
            return TwitterMapper.timeLine(result, service, paging);
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
        if (req.isMessage()) {
            postMessage(req);
            return;
        }

        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            ExecutorService pool = Executors.newCachedThreadPool();
            StatusUpdate update = new StatusUpdate(req.getText());

            // 返信の処理
            if (req.getReplyId() != null) {
                update.setInReplyToStatusId((Long) req.getReplyId());
            }

            // 引用RTの処理
            if (req.getQuoteId() != null) {
                update.setAttachmentUrl((String) req.getQuoteId());
            }

            // 画像の処理
            if (req.getImages() != null && !req.getImages().isEmpty()) {

                // 画像を並列でアップロードする
                List<Future<Long>> medias = req.getImages() //
                        .stream().map(image -> pool.submit(() -> {
                            InputStream input = new ByteArrayInputStream(image.getData());
                            return twitter.uploadMedia(image.getName(), input).getMediaId();
                        })).collect(toList());

                update.setMediaIds(medias.stream().mapToLong( //
                        (e) -> HandlingUtil.runtime(e::get)).toArray());
            }

            // センシティブな内容
            if (req.isSensitive()) {
                update.setPossiblySensitive(true);
            }

            Status status = twitter.updateStatus(update);
            Service service = getAccount().getService();
            service.getRateLimit().addInfo(GetComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Comment getComment(Identify id) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.showStatus((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(GetComment, status);

            return TwitterMapper.comment(status, service);
        });
    }

    /**
     * {@inheritDoc}
     * Parse Twitter tweet's url, like:
     * https://twitter.com/xxxx/status/[0-9]+
     */
    @Override
    public Comment getComment(String url) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Pattern regex = Pattern.compile("https://twitter.com/(.+?)/status/([0-9]+)");
            Matcher matcher = regex.matcher(url);

            if (matcher.matches()) {
                Long id = Long.parseLong(matcher.group(2));
                return getComment(new Identify(service, id));
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
            Twitter twitter = auth.getAccessor();
            Status status = twitter.favorites().createFavorite((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(LikeComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlikeComment(Identify id) {
        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.favorites().destroyFavorite((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(UnlikeComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shareComment(Identify id) {
        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.tweets().retweetStatus((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(ShareComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unshareComment(Identify id) {
        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Status status = twitter.tweets().unRetweetStatus((Long) id.getId());

            Service service = getAccount().getService();
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

            if (TwitterReactionType.Favorite.getCode().contains(type)) {
                likeComment(id);
                return;
            }
            if (TwitterReactionType.Retweet.getCode().contains(type)) {
                shareComment(id);
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

            if (TwitterReactionType.Favorite.getCode().contains(type)) {
                unlikeComment(id);
                return;
            }
            if (TwitterReactionType.Retweet.getCode().contains(type)) {
                unshareComment(id);
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
            Twitter twitter = auth.getAccessor();
            Status status = twitter.tweets().destroyStatus((Long) id.getId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(DeleteComment, status);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReactionCandidate> getReactionCandidates() {
        return TwitterMapper.reactionCandidates();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Context getCommentContext(Identify id) {
        if (id instanceof Comment) {

            // DM の場合はコンテキストの取得不可
            if (((Comment) id).getDirectMessage()) {
                Context model = new Context();
                model.setAncestors(new ArrayList<>());
                model.setDescendants(new ArrayList<>());
                return model;
            }
        }

        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();
            ExecutorService pool = Executors.newCachedThreadPool();

            TwitterComment originComment = toTwitterComment(id);
            TwitterComment comment = toTwitterComment(originComment.getDisplayComment());

            Future<List<Comment>> ancestors = null;
            Future<List<Status>> afterRecent;
            Future<List<Status>> afterWhole;
            Future<List<Status>> afterQuote;

            // ------------------------------------------------ //
            // 前の会話内容を取得
            // ------------------------------------------------ //

            if (comment.getReplyTo() != null) {
                ancestors = pool.submit(() -> {
                    return proceed(() -> {
                        List<Comment> results = new ArrayList<>();
                        Long replyId = (Long) comment.getReplyTo().getId();

                        for (int i = 0; i < 10; i++) {
                            Status status = twitter.showStatus(replyId);
                            Comment c = TwitterMapper.comment(status, service);
                            results.add(0, c);

                            if (status.getInReplyToStatusId() > 0) {
                                replyId = status.getInReplyToStatusId();
                                continue;
                            }
                            break;
                        }
                        return results;
                    });
                });
            }

            // ------------------------------------------------ //
            // 後の会話情報を取得
            // ------------------------------------------------ //
            List<Comment> descendants;

            {
                // クエリを組み上げる処理
                User user = comment.getUser();
                String mention = user.getScreenName();
                Long sinceId = (Long) comment.getId();
                Long maxId = SnowflakeUtil.ofTwitter().addHoursToID(sinceId, 2L);

                // ツイート後の二時間を対象に取得
                afterRecent = pool.submit(() -> {
                    return proceed(() -> {
                        Query query = new Query();
                        query.setSinceId(sinceId);
                        query.setMaxId(maxId);
                        query.setQuery(mention + " -RT");
                        query.setCount(200);

                        return twitter.search(query).getTweets();
                    });
                });

                // 検索可能な全期間を検索
                afterWhole = pool.submit(() -> {
                    return proceed(() -> {
                        Query query = new Query();
                        query.setSinceId(sinceId);
                        query.setQuery(mention + " -RT");
                        query.setCount(200);

                        return twitter.search(query).getTweets();
                    });
                });

                // 引用 RT のアカウントを取得
                afterQuote = pool.submit(() -> {
                    return proceed(() -> {
                        Query query = new Query();
                        query.setQuery(comment.getWebUrl() + " -RT");
                        query.setCount(200);

                        return twitter.search(query).getTweets();
                    });
                });

                // 結果を統合
                List<Status> statuses = new ArrayList<>();
                statuses.addAll(afterRecent.get());
                statuses.addAll(afterWhole.get());
                statuses = statuses.stream().distinct().collect(toList());

                // 結果として扱うステータス一覧
                List<Status> results = new ArrayList<>(afterQuote.get());

                // 返信リストを取得
                List<Long> idList = new ArrayList<>();
                idList.add(sinceId);

                while (true) {
                    List<Status> inserts = new ArrayList<>();

                    for (Status status : statuses) {
                        if (idList.contains(status.getInReplyToStatusId())) {
                            if (!results.contains(status)) {
                                inserts.add(status);
                            }
                        }
                    }

                    // 既に全て加えてあれば終了
                    if (inserts.isEmpty()) {
                        break;
                    }

                    // 返信関連の ID を一覧に加える
                    idList.addAll(inserts.stream().map(Status::getId).collect(toList()));
                    idList = idList.stream().distinct().collect(toList());

                    statuses.removeAll(inserts);
                    results.addAll(inserts);
                    inserts.clear();
                }

                descendants = results.stream()
                        .map((c) -> TwitterMapper.comment(c, service))
                        .collect(toList());
            }

            Context context = new Context();
            context.setDescendants(descendants);
            context.setAncestors((ancestors != null) ? ancestors.get() : new ArrayList<>());
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
            int count = getCountFromPage(paging, 20);
            long cursor = getCursorFromPage(paging, -1L);

            Twitter twitter = auth.getAccessor();
            PagableResponseList<UserList> lists = twitter.list()
                    .getUserListsOwnerships((Long) id.getId(), count, cursor);

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(GetChannels, lists);
            return TwitterMapper.channels(lists, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getChannelTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();
            ResponseList<Status> statues = twitter.list().getUserListStatuses(
                    (Long) id.getId(), TwitterMapper.fromPaging(paging));

            service.getRateLimit().addInfo(ChannelTimeLine, statues);
            return TwitterMapper.timeLine(statues, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getChannelUsers(Identify id, Paging paging) {
        return proceed(() -> {
            int count = getCountFromPage(paging, 20);
            long cursor = getCursorFromPage(paging, -1L);

            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            ResponseList<twitter4j.User> users = twitter.list()
                    .getUserListMembers((Long) id.getId(), count, cursor);

            service.getRateLimit().addInfo(ChannelUsers, users);
            return TwitterMapper.users(users, service, paging);
        });
    }

    /**
     * Get user following channels
     * ユーザーがフォローしているチャンネル一覧を取得
     */
    public Pageable<Channel> getUserFollowingChannel(Identify id, Paging paging) {
        return proceed(() -> {
            int count = getCountFromPage(paging, 20);
            long cursor = getCursorFromPage(paging, -1L);

            Twitter twitter = auth.getAccessor();
            ResponseList<UserList> lists = twitter.list()
                    .getUserListSubscriptions((Long) id.getId(), count, cursor);

            Service service = getAccount().getService();
            return TwitterMapper.channels(lists, service, paging);
        });
    }

    /**
     * Get user Listed channels
     * ユーザーが登録されているチャンネル一覧を取得
     */
    public Pageable<Channel> getUserListedChannel(Identify id, Paging paging) {
        return proceed(() -> {
            int count = getCountFromPage(paging, 20);
            long cursor = getCursorFromPage(paging, -1L);

            Twitter twitter = auth.getAccessor();
            PagableResponseList<UserList> lists = twitter.list()
                    .getUserListMemberships((Long) id.getId(), count, cursor);

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(GetChannels, lists);
            return TwitterMapper.channels(lists, service, paging);
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
            int count = getCountFromPage(paging, 50);
            String cursor = getCursorFromPage(paging, null);
            ExecutorService pool = Executors.newCachedThreadPool();

            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            Map<Long, User> users = new HashMap<>();
            List<DirectMessage> messages = new ArrayList<>();

            // 50 個以上も複数リクエストを行い実現
            while (count > 0) {
                int req = Math.min(count, 50);
                DirectMessageList list = (cursor == null) ?
                        twitter.getDirectMessages(req) :
                        twitter.getDirectMessages(req, cursor);

                messages.addAll(list);
                cursor = list.getNextCursor();
                count -= req;

                if (cursor == null) {
                    break;
                }
            }

            // ページング情報を作成
            CursorPaging<String> pg = CursorPaging.fromPaging(paging);
            pg.setHasNext(cursor != null);
            pg.setNextCursor(cursor);

            // ユーザー ID のリストを取得
            List<Long> userIds = new ArrayList<>();
            userIds.addAll(messages.stream().map(DirectMessage::getSenderId).collect(toList()));
            userIds.addAll(messages.stream().map(DirectMessage::getRecipientId).collect(toList()));
            userIds = userIds.stream().distinct().collect(toList());

            // 100 個で分割してリクエスト
            List<List<Long>> userIdLists = partitionList(userIds, 100);

            // ユーザー情報を並列で取得
            List<Future<List<User>>> futures = new ArrayList<>();

            for (List<Long> userIdList : userIdLists) {
                futures.add(pool.submit(() -> {
                    try {
                        long[] userIdAry = userIdList.stream().mapToLong(i -> i).toArray();
                        ResponseList<twitter4j.User> l = twitter.users().lookupUsers(userIdAry);
                        return l.stream().map(u -> TwitterMapper.user(u, service)).collect(toList());

                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }));
            }

            for (Future<List<User>> future : futures) {
                for (User user : future.get()) {
                    users.put((Long) user.getId(), user);
                }
            }

            // FIXME: 自身はすでに取得済みなのでリクエストを省略可能
            User me = getUserMeWithCache();

            List<Thread> threads = TwitterMapper.message(messages, users, me, twitter, service, pg);
            threads.sort(Comparator.comparing(Thread::getLastUpdate).reversed());

            Pageable<Thread> pageable = new Pageable<>();
            pageable.setEntities(threads);
            pageable.setPaging(pg);
            return pageable;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getMessageTimeLine(Identify id, Paging paging) {

        // TwitterThread の場合は中身を取得
        if (id instanceof TwitterThread) {
            return ((TwitterThread) id).getComments();
        }

        return getMessageThread(paging).getEntities().stream()
                .filter((th) -> th.getId().equals(id.getId()))
                .findFirst()
                //
                .map((th) -> (TwitterThread) th)
                .map(TwitterThread::getComments)
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postMessage(CommentForm req) {
        if (!req.isMessage()) {
            postComment(req);
            return;
        }

        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            ExecutorService pool = Executors.newCachedThreadPool();

            // どの DM スレッドかに送信するか？
            if (req.getReplyId() == null) {
                throw new IllegalStateException("Needs DM Thread ID.");
            }

            Long targetId = null;
            if (req.getReplyId() instanceof Long) {
                targetId = (Long) req.getReplyId();
            }
            if (req.getReplyId() instanceof Thread) {
                User me = getUserMeWithCache();
                Thread thread = (Thread) req.getReplyId();

                // 自分に対しての DM の場合はそのまま格納
                if (thread.getUsers().size() == 1) {
                    targetId = (Long) thread.getUsers().get(0).getId();

                } else {
                    // 他人に対しての DM の場合はその人の ID を取得
                    targetId = (Long) thread.getUsers().stream()
                            .filter(u -> !u.getId().equals(me.getId()))
                            .findFirst().map(Identify::getId).orElse(null);
                }
            }

            // スレッド ID が不正
            if (targetId == null) {
                throw new IllegalStateException("Invalid Thread ID.");
            }

            // 画像の処理
            List<Long> mediaIds = new ArrayList<>();
            if (req.getImages() != null && !req.getImages().isEmpty()) {

                // 画像を並列でアップロードする
                List<Future<Long>> medias = req.getImages() //
                        .stream().map(image -> pool.submit(() -> {
                            InputStream input = new ByteArrayInputStream(image.getData());
                            return twitter.uploadMedia(image.getName(), input).getMediaId();
                        })).collect(toList());

                for (Future<Long> m : medias) {
                    mediaIds.add(HandlingUtil.runtime(m::get));
                }
            }

            // メディアがない場合
            if (mediaIds.size() == 0) {
                twitter.directMessages().sendDirectMessage(
                        targetId, req.getText());

            } else {

                // メディアが複数ある場合は最後のリクエストのみコメントを記載
                int lastIndex = (mediaIds.size() - 1);
                for (Long mediaId : mediaIds) {

                    if (mediaId.equals(mediaIds.get(lastIndex))) {
                        twitter.directMessages().sendDirectMessage(
                                targetId, req.getText(), mediaId);
                    } else {

                        // 複数画像が存在する場合は先に画像情報を送信
                        twitter.directMessages().sendDirectMessage(
                                targetId, "", mediaId);
                    }
                }
            }
        });
    }

    // ============================================================== //
    // Stream
    // ============================================================== //

    /**
     * Set Home Timeline Stream
     * ホームタイムラインのイベントを取得
     * (5000 人までフォローしているユーザー専用
     * filter ストリームで誤魔化して使用
     * 鍵アカウントの情報は取得不可)
     */
    @Override
    public net.socialhub.model.service.Stream
    setHomeTimeLineStream(EventCallback callback) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();
            List<Long> idList = new ArrayList<>();

            // 自分自身を取得
            long me = (Long) getUserMeWithCache().getId();
            idList.add(me);

            // 友人の情報を取得
            IDs friendIds = twitter.getFriendsIDs(me, -1L, 4999);
            for (Long id : friendIds.getIDs()) {
                idList.add(id);
            }

            // ミュートのアカウントを取得し除外
            IDs muteIds = twitter.getMutesIDs(-1L);
            for (Long id : muteIds.getIDs()) {
                idList.remove(id);
            }

            TwitterStream stream = ((TwitterAuth) auth).getStreamAccessor();
            stream.addConnectionLifeCycleListener(new TwitterConnectionListener(callback));
            stream.addListener(new TwitterCommentListener(callback, idList, service));

            return new net.socialhub.model.service.addition
                    .twitter.TwitterStream(stream, (s) -> {
                FilterQuery q = new FilterQuery(idList.stream()
                        .mapToLong(e -> e).toArray());
                s.filter(q);
            });
        });
    }

    /**
     * Set Search Timeline Stream
     * 検索タイムラインのイベントを取得
     */
    public net.socialhub.model.service.Stream
    setSearchTimeLineStream(EventCallback callback, String query) {
        return proceed(() -> {
            Service service = getAccount().getService();
            TwitterStream stream = ((TwitterAuth) auth).getStreamAccessor();
            stream.addConnectionLifeCycleListener(new TwitterConnectionListener(callback));
            stream.addListener(new TwitterCommentListener(callback, null, service));

            return new net.socialhub.model.service.addition
                    .twitter.TwitterStream(stream, (s) -> {
                FilterQuery q = new FilterQuery(query);
                s.filter(q);
            });
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public net.socialhub.model.service.Stream
    setNotificationStream(EventCallback callback) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();
            List<Long> idList = new ArrayList<>();

            // 自分自身を取得
            User me = getUserMeWithCache();
            idList.add((Long) me.getId());

            TwitterStream stream = ((TwitterAuth) auth).getStreamAccessor();
            stream.addConnectionLifeCycleListener(new TwitterConnectionListener(callback));
            stream.addListener(new TwitterNotificationListener(callback, service, me));

            return new net.socialhub.model.service.addition
                    .twitter.TwitterStream(stream, (s) -> {
                FilterQuery q = new FilterQuery(idList.stream()
                        .mapToLong(e -> e).toArray());
                s.filter(q);
            });
        });
    }

    // ============================================================== //
    // Micro Blog
    // ============================================================== //

    /**
     * Get user pinned comments.
     * ユーザーのピンされたコメントを取得
     */
    public List<Comment> getUserPinedComments(Identify id) {
        return proceed(() -> {
            JTW v2 = getV2Client();
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            Response<Root<work.socialhub.api.response.User>> response =
                    v2.show(UsersLookupIdRequest.builder(id.getId().toString()).build());

            // ユーザーの PIN ツイートが見つからない場合は空リストを返す
            String pinnedTweetId = response.getValue().getData().getPinnedTweetId();
            if (pinnedTweetId == null) {
                return emptyList();
            }

            // 個々のツイートの取得
            ResponseList<Status> statuses = twitter.tweets().lookup(
                    Stream.of(pinnedTweetId).mapToLong(Long::parseLong).toArray());

            // モデルにマッピングして返却
            return statuses.stream().map(e -> TwitterMapper.comment(e, service))
                    .sorted(Comparator.comparing(Comment::getCreateAt).reversed())
                    .collect(toList());
        });
    }

    /**
     * Get user pinned comments. (from web client)
     * ユーザーのピンされたコメントを取得
     */
    public List<Comment> getUserPinedCommentsFromWeb(Identify id) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            if (useWebClient) {

                // 公開アカウントの場合 Web API から取得
                if (id instanceof TwitterUser) {
                    TwitterUser twuser = (TwitterUser) id;
                    if (!twuser.getProtected()) {

                        ScreenNameRequest request = new ScreenNameRequest();
                        request.setScreenName(twuser.getScreenName());

                        GraphRoot root = getWebClient().user().getUserByScreenName(request).get();
                        String[] pinTweetIds = root.getData().getUser().getLegacy().getPinnedTweetIds();

                        // ユーザーの PIN ツイートが見つからない場合は空リストを返す
                        if (pinTweetIds == null || pinTweetIds.length == 0) {
                            return emptyList();
                        }

                        // 個々のツイートの取得
                        ResponseList<Status> statuses = twitter.tweets().lookup(
                                Stream.of(pinTweetIds).mapToLong(Long::parseLong).toArray());

                        // モデルにマッピングして返却
                        return statuses.stream().map(e -> TwitterMapper.comment(e, service))
                                .sorted(Comparator.comparing(Comment::getCreateAt).reversed())
                                .collect(toList());

                    } else {
                        throw new SocialHubException("User is not public account.");
                    }
                } else {
                    throw new SocialHubException("Need twitter user object for identify.");
                }
            } else {
                throw new SocialHubException("Cannot access to user's pin tweet.");
            }
        });
    }

    // ============================================================== //
    // Others
    // ============================================================== //

    /**
     * Get Saved Search Query
     * 保存した検索を取得
     */
    public List<String> getSavedSearch() {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            ResponseList<SavedSearch> list = twitter.getSavedSearches();
            return list.stream().map(SavedSearch::getQuery).collect(toList());
        });
    }

    /**
     * Get Trends
     * トレンドを取得
     */
    public List<Trend> getTrends(Integer id) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Trends trends = twitter.getPlaceTrends(id);

            return Arrays.stream(trends.getTrends()).map(e -> {
                Trend model = new Trend();
                model.setName(e.getName());
                model.setQuery(decodeUrlEncode(e.getQuery()));

                // ボリュームが有効な場合
                if (e.getTweetVolume() > 0) {
                    model.setVolume(e.getTweetVolume());
                }
                return model;
            }).collect(toList());
        });
    }

    /**
     * Get Trend Locations
     * トレンドロケーションを取得
     */
    public List<TrendCountry> getTrendLocations() {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            ResponseList<Location> locations = twitter.getAvailableTrends();
            List<TrendCountry> results = new ArrayList<>();

            // 先に国を処理
            locations.stream()
                    .filter(e -> e.getPlaceName().equals("Country")
                            || e.getPlaceName().equals("Supername"))
                    .forEach(e -> {
                        TrendCountry model = new TrendCountry();
                        model.setLocations(new ArrayList<>());
                        model.setName(e.getName());
                        model.setId(e.getWoeid());
                        results.add(model);
                    });

            // 次にロケーションを処理
            locations.stream()
                    .filter(e -> e.getPlaceName().equals("Town"))
                    .forEach(e -> {
                        TrendCountry country = results.stream()
                                .filter(r -> r.getName().equals(e.getCountryName()))
                                .findFirst().orElse(null);

                        TrendLocation model = new TrendLocation();
                        model.setName(e.getName());
                        model.setId(e.getWoeid());
                        country.getLocations().add(model);
                    });

            return results;
        });
    }

    /**
     * Get Trends with Top Comments
     * トレンドとトップコメントを取得
     */
    public List<TrendComment> getTrendsComment(Integer id) {
        ExecutorService pool = Executors.newCachedThreadPool();
        List<Trend> trends = getTrends(id);

        // 分割して検索リクエストを送信
        List<List<Trend>> words = partitionList(trends, 10);

        return proceed(() -> {
            List<Future<List<Comment>>> futures = new ArrayList<>();
            List<Comment> comments = new ArrayList<>();

            // 分散リクエストを送信
            for (List<Trend> queries : words) {
                futures.add(pool.submit(() -> {
                    TwitterSearchBuilder builder = new TwitterSearchBuilder();
                    builder.excludeRetweets();
                    builder.minRetweets(50);
                    builder.minFaves(50);

                    TwitterSearchQuery query = new TwitterSearchQuery();
                    builder.query(query);

                    for (Trend q : queries) {
                        query.or(new TwitterSearchQuery()
                                .freeword(q.getQuery()));
                    }

                    try {
                        Pageable<Comment> results = getSearchTimeLine(
                                builder.buildQuery(), new Paging(200L));
                        return results.getEntities();

                    } catch (Exception e) {
                        logger.warn("ignored error in comment trends.");
                        logger.warn("query: " + builder.buildQuery());
                        return new ArrayList<>();
                    }
                }));
            }

            for (Future<List<Comment>> future : futures) {
                comments.addAll(future.get());
            }

            return trends.stream().map((trend) -> {
                TrendComment model = new TrendComment();
                model.setTrend(trend);

                // 一番リアクション数が多いものを取得
                model.setComment(comments.stream()
                        .map((e) -> (TwitterComment) e)
                        .filter((e) -> e.getText().getDisplayText().contains(trend.getName()))
                        .max(Comparator.comparing((a) -> a.getLikeCount() + a.getShareCount()))
                        .orElse(null));

                return model;
            }).collect(toList());
        });
    }

    /**
     * Get Users who favorites specified tweet
     * 特定のツイートをいいねしたユーザーを取得
     */
    public Pageable<User> getUsersFavoriteBy(Identify id, Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            if (useWebClient) {

                // 公開アカウントの場合 Web API から取得
                TwitterComment c = toTwitterComment(id);
                if (!((TwitterUser) c.getUser()).getProtected()) {

                    SpecifiedTweetRequest request = new SpecifiedTweetRequest();
                    request.setTweetId(c.getId().toString());
                    request.setCount(getCountFromPage(paging, 100));
                    request.setCursor(getCursorFromPage(paging, null));

                    TopLevel top = getWebClient().user().getUsersLikedBy(request).get();
                    long[] userIds = top.toUserTimeline().stream().filter(Objects::nonNull)
                            .mapToLong(e -> Long.parseLong(e.getId())).toArray();

                    CursorPaging<String> pg = CursorPaging.fromPaging(paging);
                    pg.setHasNext(top.getBottomCursor() != null);
                    pg.setNextCursor(top.getBottomCursor());
                    pg.setHasPrev(top.getTopCursor() != null);
                    pg.setPrevCursor(top.getTopCursor());

                    ResponseList<twitter4j.User> users = twitter.users().lookupUsers(userIds);
                    Pageable<User> pageable = TwitterMapper.users(users, service, paging);
                    pageable.setPaging(pg);
                    return pageable;

                } else {
                    throw new SocialHubException("User is not public account.");
                }
            } else {
                throw new SocialHubException("Cannot access to user's pin tweet.");
            }
        });
    }

    /**
     * Get Users who retweeted specified tweet
     * 特定のツイートをリツイートしたユーザーを取得
     */
    public Pageable<User> getUsersRetweetBy(Identify id, Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getAccessor();
            Service service = getAccount().getService();

            if (useWebClient) {

                // 公開アカウントの場合 Web API から取得
                TwitterComment c = toTwitterComment(id);
                if (!((TwitterUser) c.getUser()).getProtected()) {

                    SpecifiedTweetRequest request = new SpecifiedTweetRequest();
                    request.setTweetId(c.getId().toString());
                    request.setCount(getCountFromPage(paging, 100));
                    request.setCursor(getCursorFromPage(paging, null));

                    TopLevel top = getWebClient().user().getUsersRetweetedBy(request).get();
                    long[] userIds = top.toUserTimeline().stream().filter(Objects::nonNull)
                            .mapToLong(e -> Long.parseLong(e.getId())).toArray();

                    CursorPaging<String> pg = CursorPaging.fromPaging(paging);
                    pg.setHasNext(top.getBottomCursor() != null);
                    pg.setNextCursor(top.getBottomCursor());
                    pg.setHasPrev(top.getTopCursor() != null);
                    pg.setPrevCursor(top.getTopCursor());

                    ResponseList<twitter4j.User> users = twitter.users().lookupUsers(userIds);
                    Pageable<User> pageable = TwitterMapper.users(users, service, paging);
                    pageable.setPaging(pg);
                    return pageable;

                } else {
                    throw new SocialHubException("User is not public account.");
                }
            } else {
                throw new SocialHubException("Cannot access to user's pin tweet.");
            }
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
        return new TwitterRequest(getAccount());
    }

    // ============================================================== //
    // Support
    // ============================================================== //

    /**
     * ID を MiniBlogComment に変換
     */
    private TwitterComment toTwitterComment(Identify id) {

        // コメント情報を取得
        if (id instanceof TwitterComment) {
            return (TwitterComment) id;
        } else {
            Comment c = getComment(id);
            if (c instanceof TwitterComment) {
                return (TwitterComment) c;
            }
        }

        throw new IllegalStateException();
    }

    // ============================================================== //
    // Classes
    // ============================================================== //

    // コメントに対するコールバック設定
    static class TwitterCommentListener extends StatusAdapter {

        private final EventCallback listener;
        private final List<Long> userIdList;
        private final Service service;

        TwitterCommentListener(
                EventCallback listener,
                List<Long> userIdList,
                Service service) {
            this.listener = listener;
            this.userIdList = userIdList;
            this.service = service;
        }

        @Override
        public void onStatus(Status status) {
            if (listener instanceof UpdateCommentCallback) {

                // 関連 ID 意外のコメントは除外
                if (userIdList != null && !userIdList.isEmpty()) {

                    // コメントのユーザーが対象ユーザーリストに含まれるかを確認
                    if (!userIdList.contains(status.getUser().getId())) {
                        return;
                    }

                    // リプライ先も対象リストに含まれるかを確認
                    if (status.getInReplyToUserId() > 0) {
                        if (!userIdList.contains(status.getInReplyToUserId())) {
                            return;
                        }
                    }
                }

                Comment comment = TwitterMapper.comment(status, service);
                CommentEvent event = new CommentEvent(comment);
                ((UpdateCommentCallback) listener).onUpdate(event);
            }
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice delete) {
            if (listener instanceof DeleteCommentCallback) {
                if (delete.getStatusId() > 0L) {
                    IdentifyEvent event = new IdentifyEvent(delete.getStatusId());
                    ((DeleteCommentCallback) listener).onDelete(event);
                }
            }
        }
    }

    // 通信に対するコールバック設定
    static class TwitterConnectionListener implements ConnectionLifeCycleListener {

        private final EventCallback listener;

        TwitterConnectionListener(
                EventCallback listener) {
            this.listener = listener;
        }

        @Override
        public void onConnect() {
            if (listener instanceof ConnectCallback) {
                ((ConnectCallback) listener).onConnect();
            }
        }

        @Override
        public void onDisconnect() {
            if (listener instanceof DisconnectCallback) {
                ((DisconnectCallback) listener).onDisconnect();
            }
        }

        @Override
        public void onCleanUp() {

        }
    }

    // 通知に対するコールバック設定
    static class TwitterNotificationListener extends StatusAdapter {

        private final EventCallback listener;
        private final Service service;
        private final User me;

        TwitterNotificationListener(
                EventCallback listener,
                Service service,
                User me) {
            this.listener = listener;
            this.service = service;
            this.me = me;
        }

        @Override
        public void onStatus(Status status) {

            if (listener instanceof NotificationCommentCallback) {

                // そのユーザーに対しての RT の場合
                if ((status.getRetweetedStatus() != null) &&
                        (status.getRetweetedStatus().getUser().getId() == (Long) me.getId())) {
                    Comment comment = TwitterMapper.comment(status, service);

                    Notification model = new Notification(service);
                    model.setType(NotificationActionType.SHARE.getCode());
                    model.setAction(NotificationActionType.SHARE.getCode());
                    model.setComments(singletonList(comment.getDisplayComment()));
                    model.setUsers(singletonList(comment.getUser()));

                    ((NotificationCommentCallback) listener)
                            .onNotification(new NotificationEvent(model));
                    return;
                }
            }

            if (listener instanceof MentionCommentCallback) {
                String id = me.getAccountIdentify().toLowerCase();

                // リプライの場合
                if ((status.getInReplyToUserId() > 0) ||
                        status.getText().toLowerCase().contains(id)) {
                    Comment comment = TwitterMapper.comment(status, service);
                    ((MentionCommentCallback) listener).onMention(new CommentEvent(comment));
                    return;
                }
            }
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice delete) {
        }
    }

    // ============================================================== //
    // Paging
    // ============================================================== //

    private int getCountFromPage(Paging paging, int defValue) {
        return getCountFromPage(
                paging,
                defValue,
                Integer.MAX_VALUE);
    }

    private int getCountFromPage(Paging paging, int defValue, int maxValue) {
        if (paging != null) {
            if (paging.getCount() != null) {
                return Math.min(
                        maxValue,
                        paging.getCount().intValue());
            }
        }
        return Math.min(
                maxValue,
                defValue);
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
    // Utils
    // ============================================================== //

    // FIXME: TwitterException
    private static <T> T proceed(ActionCaller<T, Exception> runner) {
        try {
            return runner.proceed();
        } catch (Exception e) {
            handleTwitterException(e);
            return null;
        }
    }

    private static void proceed(ActionRunner<Exception> runner) {
        try {
            runner.proceed();
        } catch (Exception e) {
            handleTwitterException(e);
        }
    }

    private static void handleTwitterException(Exception e) {
        SocialHubException se = new SocialHubException(e);

        if (e instanceof TwitterException) {
            TwitterException te = (TwitterException) e;

            // 特殊対応エラー：リクエスト上限
            if (te.exceededRateLimitation()) {
                se.setError(ErrorType.RATE_LIMIT_EXCEEDED);
                throw se;
            }

            // その他エラーの場合はエラーメッセージ内容を作成
            if (te.isErrorMessageAvailable()) {
                se.setError(new Error(te.getErrorMessage()));
                throw se;
            }
        }
        throw se;
    }

    private String decodeUrlEncode(String str) {
        try {
            return URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    private TwitterWebClient getWebClient() {
        if (webClient == null) {
            webClient = new TwitterWebClient.Builder().build();
        }
        return webClient;
    }

    private JTW getV2Client() {
        if (v2Client == null) {
            v2Client = JTWFactory.fromTwitter4J(auth.getAccessor());
        }
        return v2Client;
    }

    public void setUseWebClient(boolean useWebClient) {
        this.useWebClient = useWebClient;
    }

    //region // Getter&Setter
    TwitterAction(Account account, ServiceAuth<Twitter> auth) {
        this.account(account);
        this.auth(auth);
    }

    TwitterAction auth(ServiceAuth<Twitter> auth) {
        this.auth = auth;
        return this;
    }
    //endregion
}
