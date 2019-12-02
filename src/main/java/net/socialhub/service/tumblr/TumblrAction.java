package net.socialhub.service.tumblr;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.*;
import net.socialhub.define.service.tumblr.TumblrIconSize;
import net.socialhub.define.service.tumblr.TumblrReactionType;
import net.socialhub.model.Account;
import net.socialhub.model.error.NotSupportedException;
import net.socialhub.model.error.SocialHubException;
import net.socialhub.model.request.CommentForm;
import net.socialhub.model.request.MediaForm;
import net.socialhub.model.service.User;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.tumblr.TumblrComment;
import net.socialhub.model.service.addition.tumblr.TumblrPaging;
import net.socialhub.model.service.addition.tumblr.TumblrUser;
import net.socialhub.model.service.support.ReactionCandidate;
import net.socialhub.model.service.support.TupleIdentify;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.Collections.singletonList;

public class TumblrAction extends AccountActionImpl {

    private ServiceAuth<JumblrClient> auth;

    // ============================================================== //
    // Account
    // ============================================================== //

    /**
     * Get User's Avatar Icon Url
     * アバター画像 URL を取得
     */
    public String getUserAvatar(String host) {
        TumblrIconSize size = TumblrIconSize.S512;
        return TumblrMapper.getAvatarUrl(host, size);
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
            String host = TumblrMapper.getUserIdentify(user.getBlogs());
            User result = TumblrMapper.user(user, service);

            // ホスト情報が存在
            if (host != null) {

                // 投稿が一つでも存在すればその投稿情報を取得
                List<Post> posts = auth.getAccessor().blogPosts(host, limit1());

                if ((posts != null) && (posts.size() > 0)) {
                    Map<String, Trail> trails = TumblrMapper.getTrailMap(posts);
                    User cover = TumblrMapper.user(posts.get(0), trails, service);
                    TumblrMapper.margeUser(result, cover);
                }
            }
            me = result;
            return result;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            ExecutorService pool = Executors.newCachedThreadPool();
            Service service = getAccount().getService();

            // デフォルトのユーザー情報の取得
            Future<User> resultFuture = pool.submit(() -> {
                Blog blog = auth.getAccessor().blogInfo((String) id.getId());
                return TumblrMapper.user(blog, service);
            });

            // カバー情報を取得するためのリクエスト
            Future<User> coverFuture = pool.submit(() -> {
                List<Post> posts = auth.getAccessor() //
                        .blogPosts((String) id.getId(), limit1());

                if ((posts != null) && (posts.size() > 0)) {
                    Map<String, Trail> trails = TumblrMapper.getTrailMap(posts);
                    return TumblrMapper.user(posts.get(0), trails, service);
                }
                return null;
            });

            User resultUser = resultFuture.get();
            User coverUser = coverFuture.get();

            TumblrMapper.margeUser(resultUser, coverUser);
            return resultUser;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void followUser(Identify id) {
        proceed(() -> {
            auth.getAccessor().follow((String) id.getId());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unfollowUser(Identify id) {
        proceed(() -> {
            auth.getAccessor().unfollow((String) id.getId());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship getRelationship(Identify id) {
        return proceed(() -> {

            // オブジェクトに格納済みなので返却
            if (id instanceof TumblrUser) {
                return ((TumblrUser) id).getRelationship();
            }

            // ユーザーの一部なのでそれを返却
            User user = getUser(id);
            if (user instanceof TumblrUser) {
                return ((TumblrUser) user).getRelationship();
            }

            throw new IllegalStateException();
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

            // TODO: 自分のアカウントのブログであるかどうか？

            Map<String, Object> params = getPagingParams(paging);
            List<Blog> blogs = auth.getAccessor().userFollowing(params);

            return TumblrMapper.usersByBlogs(blogs, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getFollowerUsers(Identify id, Paging paging) {
        return proceed(() -> {
            Service service = getAccount().getService();

            // TODO: 自分のアカウントのブログであるかどうか？

            Map<String, Object> params = getPagingParams(paging);
            List<com.tumblr.jumblr.types.User> users = auth.getAccessor() //
                    .blogFollowers((String) id.getId(), params);

            return TumblrMapper.users(users, service, paging);
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
            Map<String, Object> params = getPagingParams(paging);
            setPagingOptions(params);

            List<Post> posts = auth.getAccessor().userDashboard(params);

            return TumblrMapper.timeLine(posts, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserCommentTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Map<String, Object> params = getPagingParams(paging);
            setPagingOptions(params);

            List<Post> posts = auth.getAccessor().blogPosts((String) id.getId(), params);

            return TumblrMapper.timeLine(posts, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getUserLikeTimeLine(Identify id, Paging paging) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Map<String, Object> params = getPagingParams(paging);

            List<Post> posts = auth.getAccessor().blogLikes((String) id.getId(), params);

            return TumblrMapper.timeLine(posts, service, paging);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<Comment> getSearchTimeLine(String query, Paging paging) {
        return proceed(() -> {
            Service service = getAccount().getService();
            Map<String, Object> params = getPagingParams(paging);

            List<Post> posts = auth.getAccessor().tagged(query, params);

            return TumblrMapper.timeLine(posts, service, paging);
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
            User me = getUserMeWithCache();
            Post post;

            if (req.getImages() != null && !req.getImages().isEmpty()) {

                // PhotoPost
                PhotoPost photoPost = auth.getAccessor() //
                        .newPost((String) me.getId(), PhotoPost.class);

                for (MediaForm media : req.getImages()) {
                    Photo.ByteFile file = new Photo.ByteFile();
                    file.setBytes(media.getData());
                    file.setName(media.getName());
                    Photo photo = new Photo(file);
                    photoPost.addPhoto(photo);
                }

                photoPost.setCaption(req.getMessage());
                post = photoPost;

            } else {

                // TextPost
                TextPost textPost = auth.getAccessor() //
                        .newPost((String) me.getId(), TextPost.class);

                textPost.setBody(req.getMessage());
                post = textPost;
            }

            // Save
            post.save();
        });
    }

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

                Map<String, Trail> trails = TumblrMapper.getTrailMap(singletonList(post));
                return TumblrMapper.comment(post, trails, service);

            } else {
                throw new NotSupportedException("TupleIdentify required.");
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void likeComment(Identify id) {
        proceed(() -> {
            if (id instanceof TumblrComment) {
                String key = ((TumblrComment) id).getReblogKey();
                auth.getAccessor().like((Long) id.getId(), key);

            } else {
                throw new NotSupportedException("TumblrComment (id and reblog key only) required.");
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unlikeComment(Identify id) {
        proceed(() -> {
            if (id instanceof TumblrComment) {
                String key = ((TumblrComment) id).getReblogKey();
                auth.getAccessor().unlike((Long) id.getId(), key);

            } else {
                throw new NotSupportedException("TumblrComment (id and reblog key only) required.");
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shareComment(Identify id) {
        proceed(() -> {
            if (id instanceof TumblrComment) {
                TumblrComment comment = ((TumblrComment) id);
                String blog = comment.getUser().getScreenName();
                String key = comment.getReblogKey();

                auth.getAccessor().postReblog(blog, (Long) id.getId(), key);

            } else {
                throw new NotSupportedException("TumblrComment (id, blogName reblog key only) required.");
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unshareComment(Identify id) {
        throw new NotSupportedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reactionComment(Identify id, String reaction) {
        if (reaction != null && !reaction.isEmpty()) {
            String type = reaction.toLowerCase();

            if (TumblrReactionType.Like.getCode().contains(type)) {
                likeComment(id);
                return;
            }
            if (TumblrReactionType.Reblog.getCode().contains(type)) {
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

            if (TumblrReactionType.Like.getCode().contains(type)) {
                unlikeComment(id);
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
            if (id instanceof TumblrComment) {
                TumblrComment comment = ((TumblrComment) id);
                String blog = comment.getUser().getScreenName();

                auth.getAccessor().postDelete(blog, (Long) id.getId());

            } else {
                throw new NotSupportedException("TumblrComment (id, blog n ame only) required.");
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReactionCandidate> getReactionCandidates() {
        return TumblrMapper.reactionCandidates();
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

    private void setPagingOptions(Map<String, Object> params) {
        params.put("reblog_info", true);
    }

    // ============================================================== //
    // Utils
    // ============================================================== //

    private <T> T proceed(ActionCaller<T, Exception> runner) {
        try {
            return runner.proceed();
        } catch (Exception e) {
            handleTumblrException(e);
            return null;
        }
    }

    private void proceed(ActionRunner<Exception> runner) {
        try {
            runner.proceed();
        } catch (Exception e) {
            handleTumblrException(e);
        }
    }

    private static void handleTumblrException(Exception e) {
        throw new SocialHubException(e);
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
