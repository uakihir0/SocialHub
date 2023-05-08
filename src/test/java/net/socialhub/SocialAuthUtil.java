package net.socialhub;

import net.socialhub.core.SocialHub;
import net.socialhub.core.model.Account;
import net.socialhub.service.mastodon.action.MastodonAuth;
import net.socialhub.service.misskey.action.MisskeyAuth;
import net.socialhub.service.slack.action.SlackAuth;
import net.socialhub.service.tumblr.action.TumblrAuth;
import net.socialhub.service.twitter.action.TwitterAuth;

public class SocialAuthUtil {

    public static Account getTwitterAccount() {

        TwitterAuth auth = SocialHub.getTwitterAuth( //
                TestProperty.TwitterProperty.ConsumerKey, //
                TestProperty.TwitterProperty.ConsumerSecret);

        return auth.getAccountWithAccessToken( //
                TestProperty.TwitterProperty.AccessToken, //
                TestProperty.TwitterProperty.AccessSecret);
    }

    public static Account getMastodonAccount() {

        MastodonAuth auth = SocialHub.getMastodonAuth( //
                TestProperty.MastodonProperty.Host);

        return auth.getAccountWithAccessToken( //
                TestProperty.MastodonProperty.AccessToken,
                null,
                null
        );
    }

    public static Account getPleromaAccount() {
        MastodonAuth auth = SocialHub.getPleromaAuth( //
                TestProperty.PleromaProperty.Host);

        return auth.getAccountWithAccessToken( //
                TestProperty.PleromaProperty.AccessToken,
                null,
                null
        );
    }

    public static Account getPixelFedAccount() {
        MastodonAuth auth = SocialHub.getPixelFedAuth( //
                TestProperty.PixelFedProperty.Host);

        return auth.getAccountWithAccessToken( //
                TestProperty.PixelFedProperty.AccessToken,
                null,
                null
        );
    }

    public static Account getMisskeyAccount() {
        MisskeyAuth auth = SocialHub.getMisskeyAuth( //
                TestProperty.MisskeyProperty.Host);

        auth.setClientInfo(
                TestProperty.MisskeyProperty.ClientId,
                TestProperty.MisskeyProperty.ClientSecret);

        return auth.getAccountWithAccessToken( //
                TestProperty.MisskeyProperty.AccessToken);
    }

    public static Account getTumblrAccount() {

        TumblrAuth auth = SocialHub.getTumblrAuth(
                TestProperty.TumblrProperty.ConsumerKey, //
                TestProperty.TumblrProperty.ConsumerSecret);

        return auth.getAccountWithAccessToken( //
                TestProperty.TumblrProperty.AccessToken, //
                TestProperty.TumblrProperty.AccessSecret);
    }

    public static Account getSlackAccount() {

        SlackAuth auth = SocialHub.getSlackAuth( //
                TestProperty.SlackProperty.ClientId, //
                TestProperty.SlackProperty.ClientSecret);

        return auth.getAccountWithToken( //
                TestProperty.SlackProperty.Token);
    }
}
