package net.socialhub.service.tumblr;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.exceptions.JumblrException;
import net.socialhub.define.service.tumblr.TumblrIconSize;
import net.socialhub.model.Account;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.AccountActionImpl;
import net.socialhub.utils.LimitMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TumblrAction extends AccountActionImpl {

    private ServiceAuth<JumblrClient> auth;

    /** User Icon Cache Data */
    private Map<String, String> iconCache = Collections.synchronizedMap(new LimitMap<>(400));

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
            com.tumblr.jumblr.types.User user = auth.getAccessor().user();

            // アイコンキャッシュから取得
            Map<String, String> iconMap = new HashMap<>();
            String host = TumblrMapper.getUserIdentify(user.getBlogs());
            getAndSetCacheIconMap(host, iconMap);

            return TumblrMapper.user(user, iconMap, service);
        });
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

    /**
     * アイコンキャッシュ処理
     */
    private void getAndSetCacheIconMap(String host, Map<String, String> iconMap) {
        String url = iconCache.get(host);

        if (url != null) {
            iconMap.put(host, url);

        } else {
            Integer size = TumblrIconSize.S512.getSize();
            String avatar = auth.getAccessor().blogAvatar(host, size);
            iconCache.put(host, avatar);
            iconMap.put(host, avatar);
        }
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
