package net.socialhub;

import net.socialhub.j2objc.J2ObjcExtension;
import net.socialhub.j2objc.J2ObjcExtensions;
import net.socialhub.service.Supports;
import net.socialhub.service.facebook.FacebookAuth;
import net.socialhub.service.mastodon.MastodonAuth;
import net.socialhub.service.slack.SlackAuth;
import net.socialhub.service.tumblr.TumblrAuth;
import net.socialhub.service.twitter.TwitterAuth;

/**
 * Top Object for SocialHub
 * アプリのトップオブジェクト
 * (基本的にこのクラスからアクセスできるように設計)
 */
public class SocialHub {
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
     * Mastodon の認証オブジェクトを取得
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
     * サポートサービスを取得
     */
    public static Supports getSupportServices() {
        return new Supports();
    }

    public static void init() {
        J2ObjcExtension.initialize( //
                J2ObjcExtensions.Standard.Hmac //
        );
    }
}
