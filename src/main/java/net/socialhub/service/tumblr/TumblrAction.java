package net.socialhub.service.tumblr;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;
import net.socialhub.define.service.tumblr.TumblrIconSize;
import net.socialhub.define.service.tumblr.TumblrReactionType;
import net.socialhub.model.Account;
import net.socialhub.model.error.NotSupportedException;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.tumblr.TumblrComment;
import net.socialhub.model.service.addition.tumblr.TumblrPaging;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.model.service.support.TupleIdentify;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;
import net.socialhub.utils.LimitMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TumblrAction extends AccountActionImpl {

    private ServiceAuth<JumblrClient> auth;

    /**
     * User Icon Cache Data
     */
    private Map<String, String> iconCache = Collections.synchronizedMap(new LimitMap<>(400));

    // ============================================================== //
    // Account
    // ============================================================== //

    /**
     * Get User's Avatar Icon Url
     * アバター画像 URL を取得
     */
    public String getUserAvatar(String host) {
        Integer size = TumblrIconSize.S512.getSize();
        return auth.getAccessor().blogAvatar(host, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserMe() {
        return proceed(() -> {
            Service service = getAccount().getService();
            com.tumblr.jumblr.types.User user = auth.getAccessor().user();

            // アイコンキャッシュから取得
            Map<String, String> iconMap = new HashMap<>();
            String host = TumblrMapper.getUserIdentify(user.getBlogs());
            getAndSetCacheIconMap(host, iconMap);

            // ホスト情報が存在
            if (host != null) {

                // 投稿が一つでも存在すればその投稿情報を取得
                List<Post> posts = auth.getAccessor().blogPosts(host, limit1());
                if ((posts != null) && (posts.size() > 0)) {
                    return TumblrMapper.user(posts.get(0), iconMap, service);
                }
            }

            return TumblrMapper.user(user, iconMap, service);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Map<String, String> iconMap = new HashMap<>();

            // 投稿情報からユーザー情報を作成する場合
            List<Post> posts = auth.getAccessor().blogPosts((String) id.getId(), limit1());
            if ((posts != null) && (posts.size() > 0)) {
                Post post = posts.get(0);

                String host = TumblrMapper.getBlogIdentify(post.getBlog());
                getAndSetCacheIconMap(host, iconMap);
                return TumblrMapper.user(posts.get(0), iconMap, service);
            }

            // ブログ一般情報からユーザー情報を作成する場合 (投稿情報が存在しない場合)
            Blog blog = auth.getAccessor().blogInfo((String) id.getId());

            String host = TumblrMapper.getBlogIdentify(blog);
            getAndSetCacheIconMap(host, iconMap);
            return TumblrMapper.user(blog, iconMap, service);
        });
    }

    // ============================================================== //
    // User
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getFollowingUsers(Identify id, Paging paging) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Map<String, String> iconMap = new HashMap<>();

            // TODO: 自分のアカウントのブログであるかどうか？

            Map<String, Object> params = getPagingParams(paging);
            List<Blog> blogs = auth.getAccessor().userFollowing(params);

            // アイコン情報の取得
            blogs.parallelStream().forEach((blog) -> {
                String host = TumblrMapper.getBlogIdentify(blog);
                getAndSetCacheIconMap(host, iconMap);
            });

            return TumblrMapper.usersByBlogs(blogs, iconMap, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getFollowerUsers(Identify id, Paging paging) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Map<String, String> iconMap = new HashMap<>();

            // TODO: 自分のアカウントのブログであるかどうか？

            Map<String, Object> params = getPagingParams(paging);
            List<com.tumblr.jumblr.types.User> users = //
                    auth.getAccessor().blogFollowers((String) id.getId(), params);

            // アイコン情報の取得
            users.parallelStream().forEach((user) -> {
                String host = TumblrMapper.getUserIdentify(user.getBlogs());
                getAndSetCacheIconMap(host, iconMap);
            });

            return TumblrMapper.users(users, iconMap, service, paging);
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
            Service service = getAccount().getService();
            Map<String, String> iconMap = new HashMap<>();

            Map<String, Object> params = getPagingParams(paging);
            List<Post> posts = auth.getAccessor().userDashboard(params);

            // アイコン情報の取得
            posts.parallelStream().forEach((post) -> {
                String host = TumblrMapper.getBlogIdentify(post.getBlog());
                getAndSetCacheIconMap(host, iconMap);
            });

            return TumblrMapper.timeLine(posts, iconMap, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserCommentTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Map<String, String> iconMap = new HashMap<>();

            Map<String, Object> params = getPagingParams(paging);
            List<Post> posts = auth.getAccessor().blogPosts((String) id.getId(), params);

            // アイコン情報の取得
            posts.parallelStream().forEach((post) -> {
                String host = TumblrMapper.getBlogIdentify(post.getBlog());
                getAndSetCacheIconMap(host, iconMap);
            });

            return TumblrMapper.timeLine(posts, iconMap, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserLikeTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Map<String, String> iconMap = new HashMap<>();

            Map<String, Object> params = getPagingParams(paging);
            List<Post> posts = auth.getAccessor().blogLikes((String) id.getId(), params);

            // アイコン情報の取得
            posts.parallelStream().forEach((post) -> {
                String host = TumblrMapper.getBlogIdentify(post.getBlog());
                getAndSetCacheIconMap(host, iconMap);
            });

            return TumblrMapper.timeLine(posts, iconMap, service, paging);
        });
    }


    // ============================================================== //
    // Comment
    // ============================================================== //

    /**
     * {@inheritDoc}
     */
    @Override
    public Comment getComment(Identify id) {
        return proceed(() -> {
            if (id instanceof TupleIdentify) {
                TupleIdentify tuple = ((TupleIdentify) id);
                Service service = getAccount().getService();
                Post post = auth.getAccessor().blogPost( //
                        (String) tuple.getSubId(), (Long) tuple.getId());

                Map<String, String> iconMap = new HashMap<>();
                String host = TumblrMapper.getBlogIdentify(post.getBlog());
                getAndSetCacheIconMap(host, iconMap);

                return TumblrMapper.comment(post, iconMap, service);

            } else {
                throw new NotSupportedException(
                        "TupleIdentify required.");
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void like(Identify id) {
        proceed(() -> {
            if (id instanceof TumblrComment) {
                String key = ((TumblrComment) id).getReblogKey();
                auth.getAccessor().like((Long) id.getId(), key);

            } else {
                throw new NotSupportedException(
                        "TumblrComment (id and reblogKey only) required.");
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlike(Identify id) {
        proceed(() -> {
            if (id instanceof TumblrComment) {
                String key = ((TumblrComment) id).getReblogKey();
                auth.getAccessor().unlike((Long) id.getId(), key);

            } else {
                throw new NotSupportedException(
                        "TumblrComment (id and reblogKey only) required.");
            }
        });
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void retweet(Identify id) {
        proceed(() -> {
            if (id instanceof TumblrComment) {
                TumblrComment comment = ((TumblrComment) id);
                String blog = comment.getUser().getScreenName();
                String key = comment.getReblogKey();

                auth.getAccessor().postReblog(blog, (Long) id.getId(), key);

            } else {
                throw new NotSupportedException(
                        "TumblrComment (id, blogName reblogKey only) required.");
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unretweet(Identify id) {
        proceed(() -> {
            if (id instanceof TumblrComment) {
                TumblrComment comment = ((TumblrComment) id);
                String blog = comment.getUser().getScreenName();

                auth.getAccessor().postDelete(blog, (Long) id.getId());

            } else {
                throw new NotSupportedException(
                        "TumblrComment (id, blogName only) required.");
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reaction(Identify id, String reaction) {
        if (reaction != null && !reaction.isEmpty()) {
            String type = reaction.toLowerCase();

            if (TumblrReactionType.Like.getCode().contains(type)) {
                like(id);
                return;
            }
            if (TumblrReactionType.Reblog.getCode().contains(type)) {
                retweet(id);
                return;
            }
        }
        throw new NotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unreaction(Identify id, String reaction) {
        if (reaction != null && !reaction.isEmpty()) {
            String type = reaction.toLowerCase();

            if (TumblrReactionType.Like.getCode().contains(type)) {
                unlike(id);
                return;
            }
            if (TumblrReactionType.Reblog.getCode().contains(type)) {
                unretweet(id);
                return;
            }
        }
        throw new NotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReactionCandidate> getReactionCandidates() {
        return TumblrMapper.reactionCandidates();
    }

    // ============================================================== //
    // Cache
    // ============================================================== //

    /**
     * アイコンキャッシュ処理
     */
    private void getAndSetCacheIconMap(String host, Map<String, String> iconMap) {
        String url = iconCache.get(host);

        if (url != null) {
            iconMap.put(host, url);

        } else {
            String avatar = getUserAvatar(host);
            iconCache.put(host, avatar);
            iconMap.put(host, avatar);
        }
    }

    // ============================================================== //
    // Paging
    // ============================================================== //

    private Map<String, Object> getPagingParams(Paging paging) {
        Map<String, Object> params = new HashMap<>();

        if (paging != null) {
            if (paging.getCount() != null) {
                params.put("limit", paging.getCount());
            }

            if (paging instanceof TumblrPaging) {
                TumblrPaging pg = (TumblrPaging) paging;

                if (pg.getSinceId() != null) {
                    params.put("since_id", pg.getSinceId());
                }
                if (pg.getOffset() != null) {
                    params.put("offset", pg.getOffset());
                }
            }
        }
        return params;
    }


    // ============================================================== //
    // Request Samples
    // ============================================================== //

    private Map<String, Object> limit1() {
        Map<String, Object> params = new HashMap<>();
        params.put("limit", 1);
        return params;
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
