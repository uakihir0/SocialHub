package net.socialhub;

import net.socialhub.SocialHub;
import net.socialhub.TestProperty;
import net.socialhub.model.Account;
import net.socialhub.service.facebook.FacebookAuth;
import net.socialhub.service.mastodon.MastodonAuth;
import net.socialhub.service.slack.SlackAuth;
import net.socialhub.service.twitter.TwitterAuth;

public class SocialAuthUtil {

    public static Account getTwitterAccount() {

        TwitterAuth auth = SocialHub.getTwitterAuth( //
                TestProperty.TwitterProperty.ConsumerKey, //
                TestProperty.TwitterProperty.ConsumerSecret);

        return auth.getAccountWithAccessToken( //
                TestProperty.TwitterProperty.AccessToken, //
                TestProperty.TwitterProperty.AccessSecret);
    }

    public static Account getFacebookAccount() {

        FacebookAuth auth = SocialHub.getFacebookAuth( //
                TestProperty.FacebookProperty.AppId, //
                TestProperty.FacebookProperty.AppSecret);

        return auth.getAccountWithAccessToken( //
                TestProperty.FacebookProperty.AccessToken);
    }

    public static Account getMastodonAccount() {

        MastodonAuth auth = SocialHub.getMastodonAuth( //
                TestProperty.MastodonProperty.Host);

        return auth.getAccountWithAccessToken( //
                TestProperty.MastodonProperty.AccessToken);
    }

    public static Account getSlackAccount() {

        SlackAuth auth = SocialHub.getSlackAuth( //
                TestProperty.SlackProperty.ClientId, //
                TestProperty.SlackProperty.ClientSecret);

        return auth.getAccountWithToken( //
                TestProperty.SlackProperty.Token);
    }
}
