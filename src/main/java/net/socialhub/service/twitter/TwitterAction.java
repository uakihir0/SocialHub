package net.socialhub.service.twitter;

import net.socialhub.define.MediaType;
import net.socialhub.define.service.twitter.TwitterReactionType;
import net.socialhub.define.service.twitter.TwitterSearchBuilder;
import net.socialhub.define.service.twitter.TwitterSearchQuery;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.error.NotSupportedException;
import net.socialhub.model.request.CommentForm;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Relationship;
import net.socialhub.model.service.Thread;
import net.socialhub.model.service.Trend;
import net.socialhub.model.service.User;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.twitter.TwitterComment;
import net.socialhub.model.service.event.DeleteCommentEvent;
import net.socialhub.model.service.event.UpdateCommentEvent;
import net.socialhub.model.service.paging.CursorPaging;
import net.socialhub.model.service.paging.IndexPaging;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.model.service.support.TrendComment;
import net.socialhub.model.service.support.TrendCountry;
import net.socialhub.model.service.support.TrendCountry.TrendLocation;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;
import net.socialhub.service.action.RequestAction;
import net.socialhub.service.action.callback.DeleteCommentCallback;
import net.socialhub.service.action.callback.EventCallback;
import net.socialhub.service.action.callback.UpdateCommentCallback;
import net.socialhub.utils.HandlingUtil;
import net.socialhub.utils.MapperUtil;
import net.socialhub.utils.SnowflakeUtil;
import twitter4j.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.stream.Collectors.toList;
import static net.socialhub.define.action.OtherActionType.*;
import static net.socialhub.define.action.TimeLineActionType.*;
import static net.socialhub.define.action.UsersActionType.*;
import static net.socialhub.utils.CollectionUtil.partitionList;

/**
 * Twitter Actions
 * (All Actions)
 */
public class TwitterAction extends AccountActionImpl {

    private static Logger logger = Logger.getLogger(TwitterAction.class);

    private ServiceAuth<Twitter> auth;

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
        proceed(() -> {
            Twitter twitter = auth.getAccessor();
            ExecutorService pool = Executors.newCachedThreadPool();
            StatusUpdate update = new StatusUpdate(req.getMessage());

            // 返信の処理
            if (req.getReplyId() != null) {
                update.setInReplyToStatusId((Long) req.getReplyId());
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
            if (req.getSensitive() != null && req.getSensitive()) {
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

            // ユーザー ID のリストを取得
            List<Long> userIds = new ArrayList<>();
            userIds.addAll(messages.stream().map(DirectMessage::getSenderId).collect(toList()));
            userIds.addAll(messages.stream().map(DirectMessage::getRecipientId).collect(toList()));
            userIds = userIds.stream().distinct().collect(toList());

            // 100 個で分割してリクエスト
            List<List<Long>> userIdLists = partitionList(userIds, 100);

            // ユーザー情報を並列で取得
            List<Future<List<User>>> futures = new ArrayList<>();
            ExecutorService pool = Executors.newCachedThreadPool();

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

            // FIXME: 自分自身はすでに取得済みなのでリクエストを省略可能
            List<Thread> threads = TwitterMapper.message(messages, users, getUserMeWithCache(), service);
            threads.sort(Comparator.comparing(Thread::getLastUpdate).reversed());

            Pageable<Thread> pageable = new Pageable<>();
            pageable.setEntities(threads);
            return pageable;
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
            IDs ids = twitter.getFriendsIDs(me, -1L, 4999);
            for (Long id : ids.getIDs()) {
                idList.add(id);
            }

            TwitterStream stream = ((TwitterAuth) auth).getStreamAccessor();
            stream.addListener(new TwitterCommentsListener(callback, idList, service));

            return new net.socialhub.model.service.addition
                    .twitter.TwitterStream(stream, (s) -> {
                FilterQuery q = new FilterQuery(idList.stream()
                        .mapToLong(e -> e).toArray());
                stream.filter(q);
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
            stream.addListener(new TwitterCommentsListener(callback, null, service));

            return new net.socialhub.model.service.addition
                    .twitter.TwitterStream(stream, (s) -> {
                FilterQuery q = new FilterQuery(query);
                stream.filter(q);
            });
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
                        .filter((e) -> e.getText().getText().contains(trend.getName()))
                        .max(Comparator.comparing((a) -> a.getLikeCount() + a.getShareCount()))
                        .orElse(null));

                return model;
            }).collect(toList());
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

    static class TwitterCommentsListener extends StatusAdapter {

        private EventCallback listener;
        private List<Long> idList;
        private Service service;

        TwitterCommentsListener(
                EventCallback listener,
                List<Long> idList,
                Service service) {
            this.listener = listener;
            this.service = service;
            this.idList = idList;
        }

        @Override
        public void onStatus(Status status) {
            if (listener instanceof UpdateCommentCallback) {

                // 関連 ID 意外のコメントは除外
                if (idList != null && !idList.isEmpty()) {
                    if (!idList.contains(status.getId())) {
                        return;
                    }
                }
                Comment comment = TwitterMapper.comment(status, service);
                UpdateCommentEvent event = new UpdateCommentEvent(comment);
                ((UpdateCommentCallback) listener).onUpdate(event);
            }
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice delete) {
            if (listener instanceof DeleteCommentCallback) {
                if (delete.getStatusId() > 0L) {
                    DeleteCommentEvent event = new DeleteCommentEvent(delete.getStatusId());
                    ((DeleteCommentCallback) listener).onDelete(event);
                }
            }
        }
    }

    // ============================================================== //
    // Paging
    // ============================================================== //

    private int getCountFromPage(Paging paging, int defValue) {
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
    // Utils
    // ============================================================== //

    // FIXME: TwitterException
    private <T> T proceed(ActionCaller<T, Exception> runner) {
        try {
            return runner.proceed();
        } catch (Exception e) {
            handleTwitterException(e);
            return null;
        }
    }

    private void proceed(ActionRunner<Exception> runner) {
        try {
            runner.proceed();
        } catch (Exception e) {
            handleTwitterException(e);
        }
    }

    private static void handleTwitterException(Exception e) {
        System.out.println(e.getMessage());
    }

    private String decodeUrlEncode(String str) {
        try {
            return URLDecoder.decode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
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
