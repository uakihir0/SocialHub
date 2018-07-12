package net.socialhub;

import net.socialhub.j2objc.security.HmacProvider;
import net.socialhub.model.Account;
import net.socialhub.model.service.User;
import net.socialhub.service.facebook.FacebookAuth;
import net.socialhub.service.mastodon.MastodonAuth;
import net.socialhub.service.slack.SlackAuth;
import net.socialhub.service.twitter.TwitterAuth;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Base64;

public class SocialHubTest {

    @Before
    public void before() {
        TestProperty.before();
    }

    @Test
    public void testHmacSHA1() {

        Security.addProvider(new HmacProvider());

        String token = "Token";
        String message = "Message";

        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec = new SecretKeySpec(token.getBytes(), "HmacSHA1");
            mac.init(spec);

            byte[] bytes = mac.doFinal(message.getBytes());
            System.out.println(Base64.getEncoder().encodeToString(bytes));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetUserMeTwitter() {

        Security.addProvider(new HmacProvider());

        TwitterAuth auth = SocialHub.getTwitterAuth( //
                TestProperty.TwitterProperty.ConsumerKey, //
                TestProperty.TwitterProperty.ConsumerSecret);

        Account account = auth.getAccountWithAccessToken( //
                TestProperty.TwitterProperty.AccessToken, //
                TestProperty.TwitterProperty.AccessSecret);

        User user = account.getAction().getUserMe();
        System.out.println(user.getName());
    }

    @Test
    public void testGetUserMeFacebook() {

        Security.addProvider(new HmacProvider());

        FacebookAuth auth = SocialHub.getFacebookAuth( //
                TestProperty.FacebookProperty.AppId, //
                TestProperty.FacebookProperty.AppSecret);

        Account account = auth.getAccountWithAccessToken( //
                TestProperty.FacebookProperty.AccessToken);

        User user = account.getAction().getUserMe();
        System.out.println(user.getName());
    }

    @Test
    public void testGetUserMastodon() {

        MastodonAuth auth = SocialHub.getMastodonAuth( //
                TestProperty.MastodonProperty.Host);

        Account account = auth.getAccountWithAccessToken( //
                TestProperty.MastodonProperty.AccessToken);

        User user = account.getAction().getUserMe();
        System.out.println(user.getName());
    }

    @Test
    public void testGetUserSlack() {

        SlackAuth auth = SocialHub.getSlackAuth( //
                TestProperty.SlackProperty.ClientId, //
                TestProperty.SlackProperty.ClientSecret);

        Account account = auth.getAccountWithToken( //
                TestProperty.SlackProperty.Token);

        User user = account.getAction().getUserMe();
        System.out.println(user.getName());
    }
}
