package net.socialhub.service.mastodon;

import net.socialhub.service.mastodon.support.MastodonInstances;

public class MastodonSupport {

    /**
     * インスタンスを取得するための API クライアントの取得
     * https://instances.social/
     */
    public static MastodonInstances getMastodonInstances(String accessToken) {
        return new MastodonInstances(accessToken);
    }
}
