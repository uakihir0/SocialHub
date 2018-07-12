package net.socialhub;

import net.socialhub.j2objc.J2ObjcExtension;
import net.socialhub.j2objc.J2ObjcExtensions;
import net.socialhub.service.facebook.FacebookAuth;
import net.socialhub.service.mastodon.MastodonAuth;
import net.socialhub.service.slack.SlackAuth;
import net.socialhub.service.twitter.TwitterAuth;

public class SocialHub {

    /**
     * Twitter の認証オブジェクトを取得
     */
    public static TwitterAuth getTwitterAuth(String consumerKey, String consumerSecret) {
        return new TwitterAuth(consumerKey, consumerSecret);
    }

    /**
     * Facebook の認証オブジェクトを取得
     */
    public static FacebookAuth getFacebookAuth(String appId, String appSecret) {
        return new FacebookAuth(appId, appSecret);
    }

    /**
     * Mastodon の認証オブジェクトを取得
     */
    public static MastodonAuth getMastodonAuth(String host) {
        return new MastodonAuth(host);
    }

    /**
     * Slack の認証オブジェクトを取得
     */
    public static SlackAuth getSlackAuth(String clientId, String clientSecret) {
        return new SlackAuth(clientId, clientSecret);
    }


    public static void init() {
        J2ObjcExtension.initialize( //
                J2ObjcExtensions.Standard.Hmac //
        );
    }
}
