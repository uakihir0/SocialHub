package net.socialhub.service;

import net.socialhub.service.support.InstancesService;

public class Supports {

    /**
     * Mastodon インスタンスを取得するための API クライアントの取得
     * https://instances.social/
     */
    public InstancesService getMastodonInstances(String accessToken) {
        return new InstancesService(accessToken);
    }
}
