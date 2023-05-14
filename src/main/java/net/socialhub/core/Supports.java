package net.socialhub.core;

import net.socialhub.service.mastodon.support.MsInstancesService;
import net.socialhub.service.misskey.support.MiInstancesService;

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
