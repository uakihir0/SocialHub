package net.socialhub.service.tumblr;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Post;
import net.socialhub.define.service.tumblr.TumblrIconSize;
import net.socialhub.model.Account;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.tumblr.TumblrPaging;
import net.socialhub.model.service.paging.IndexPaging;
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
                    if (pg.getPage() != null && pg.getCount() != null) {
                        params.put("offset", ((pg.getPage() - 1) * pg.getCount()));
                    }
                }
            }

            List<Post> posts = auth.getAccessor().userDashboard(params);

            // アイコン情報の取得
            posts.parallelStream().forEach((post) -> {
                String host = TumblrMapper.getBlogIdentify(post.getBlog());
                getAndSetCacheIconMap(host, iconMap);
            });

            return TumblrMapper.timeLine(posts, iconMap, service, paging);
        });
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
