package net.socialhub;

import net.socialhub.j2objc.J2ObjcExtension;
import net.socialhub.j2objc.J2ObjcExtensions;
import net.socialhub.service.Supports;
import net.socialhub.service.Utils;
import net.socialhub.service.facebook.FacebookAuth;
import net.socialhub.service.mastodon.MastodonAuth;
import net.socialhub.service.misskey.MisskeyAuth;
import net.socialhub.service.slack.SlackAuth;
import net.socialhub.service.tumblr.TumblrAuth;
import net.socialhub.service.twitter.TwitterAuth;

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
     * Get Facebook Authentication Model
     * Facebook の認証オブジェクトを取得
     */
    public static FacebookAuth getFacebookAuth(String appId, String appSecret) {
        return new FacebookAuth(appId, appSecret);
    }

    /**
     * Get Mastodon Authentication Model
     * (Also can use as PixelFed and Pleroma)
     * Mastodon の認証オブジェクトを取得
     * PixelFed と Pleroma にも使用可能
     */
    public static MastodonAuth getMastodonAuth(String host) {
        return new MastodonAuth(host);
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

    public static void init() {
        J2ObjcExtension.initialize( //
                J2ObjcExtensions.Standard.Hmac //
        );
    }
}
