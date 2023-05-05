package net.socialhub.all;

import net.socialhub.misskey.support.MiInstancesService;
import net.socialhub.mastodon.support.MsInstancesService;

public class Supports {

    /**
     * Mastodon インスタンスを取得するための API クライアントの取得
     * https://instances.social/
     */
    public MsInstancesService getMastodonInstances(String accessToken) {
        return new MsInstancesService(accessToken);
    }

    /**
     * Misskey インスタンスを取得するための API クライアントの取得
     * https://join.misskey.page/ja/
     */
    public MiInstancesService getMisskeyInstances() {
        return new MiInstancesService();
    }
}
