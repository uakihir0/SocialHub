package net.socialhub.core;

import net.socialhub.core.define.ServiceType;
import net.socialhub.service.mastodon.action.MastodonAuth;
import net.socialhub.service.misskey.action.MisskeyAuth;
import net.socialhub.service.slack.action.SlackAuth;
import net.socialhub.service.tumblr.action.TumblrAuth;
import net.socialhub.service.twitter.action.TwitterAuth;

/**
 * Top Object for SocialHub
 * アプリのトップオブジェクト
 * (基本的にこのクラスからアクセスできるように設計)
 */
public final class SocialHub {
    private SocialHub() {
    }

    /**
     * Get Twitter Authentication Model
     * Twitter の認証オブジェクトを取得
     */
    public static TwitterAuth getTwitterAuth(String consumerKey, String consumerSecret) {
        return new TwitterAuth(consumerKey, consumerSecret);
    }

    /**
     * Get Mastodon Authentication Model
     * Mastodon と認証オブジェクトを取得
     */
    public static MastodonAuth getMastodonAuth(String host) {
        return new MastodonAuth(host, ServiceType.Mastodon);
    }

    /**
     * Get Pleroma Authentication Model
     * Pleroma と認証オブジェクトを取得
     */
    public static MastodonAuth getPleromaAuth(String host) {
        return new MastodonAuth(host, ServiceType.Pleroma);
    }

    /**
     * Get Pixel Authentication Model
     * Pixel と認証オブジェクトを取得
     */
    public static MastodonAuth getPixelFedAuth(String host) {
        return new MastodonAuth(host, ServiceType.PixelFed);
    }

    /**
     * Get Slack Authentication Model
     * Slack の認証オブジェクトを取得
     */
    public static SlackAuth getSlackAuth(String clientId, String clientSecret) {
        return new SlackAuth(clientId, clientSecret);
    }

    /**
     * Get Tumblr Authentication Model
     * Tumblr の認証オブジェクトを取得
     */
    public static TumblrAuth getTumblrAuth(String consumerKey, String consumerSecret) {
        return new TumblrAuth(consumerKey, consumerSecret);
    }

    /**
     * Get Misskey Authentication Model
     * Misskey の認証オブジェクトを取得
     */
    public static MisskeyAuth getMisskeyAuth(String host) {
        return new MisskeyAuth(host);
    }

    /**
     * Get Mastodon Compatible Authentication Model
     * Mastodon と互換認証オブジェクトを取得
     *
     * @see SocialHub#getMastodonAuth
     * @see SocialHub#getPixelFedAuth
     * @see SocialHub#getPleromaAuth
     * @deprecated Use only services other than Mastodon, PixelFed, and Pleroma
     * 互換性の問題があるため、Mastodon, PixelFed, Pleroma の場合すでに定義された関数を使用推奨
     */
    @Deprecated
    public static MastodonAuth getMastodonCompatibleAuth(String host) {
        // Mastodon の互換の場合は、サービスは未指定
        return new MastodonAuth(host, null);
    }

    /**
     * Get Support Services
     * サポートサービスを取得
     */
    public static Supports getSupportServices() {
        return new Supports();
    }

    /**
     * Get Util Services
     * ユーティリティを取得
     */
    public static Utils getUtilServices() {
        return new Utils();
    }
}
