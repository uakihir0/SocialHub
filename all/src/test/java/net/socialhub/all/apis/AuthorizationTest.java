package net.socialhub.all.apis;

import net.socialhub.all.TestProperty;
import net.socialhub.mastodon.define.MastodonScope;
import net.socialhub.slack.define.SlackScope;
import net.socialhub.core.model.Account;
import net.socialhub.mastodon.action.MastodonAuth;
import net.socialhub.slack.action.SlackAuth;
import net.socialhub.tumblr.action.TumblrAuth;
import net.socialhub.twitter.action.TwitterAuth;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorizationTest extends AbstractApiTest {

    @Test
    public void testTwitterAuthorizationUrl() {

        TwitterAuth auth = SocialHub.getTwitterAuth(
                TestProperty.TwitterProperty.ConsumerKey,
                TestProperty.TwitterProperty.ConsumerSecret);

        System.out.println(auth.getAuthorizationURL(
                TestProperty.TwitterProperty.RedirectUrl));
    }

    @Test
    public void testMastodonAppRegister() {

        MastodonAuth auth = SocialHub.getMastodonAuth(
                TestProperty.MastodonProperty.Host);

        auth.requestClientApplication("SocialHub", null,
                TestProperty.MastodonProperty.RedirectUrl,
                MastodonScope.FULL_ACCESS);

        System.out.println(auth.getClientId());
        System.out.println(auth.getClientSecret());
    }

    @Test
    public void testMastodonAuthorizationUrl() {

        MastodonAuth auth = SocialHub.getMastodonAuth(
                TestProperty.MastodonProperty.Host);

        auth.setClientInfo(
                TestProperty.MastodonProperty.ClientId,
                TestProperty.MastodonProperty.ClientSecret
        );

        System.out.println(auth.getAuthorizationURL(
                TestProperty.MastodonProperty.RedirectUrl,
                MastodonScope.FULL_ACCESS
        ));
    }


    @Test
    public void testMastodonAuthorizeWithCode() {

        MastodonAuth auth = SocialHub.getMastodonAuth(
                TestProperty.MastodonProperty.Host);

        auth.setClientInfo(
                TestProperty.MastodonProperty.ClientId,
                TestProperty.MastodonProperty.ClientSecret
        );

        String code = "PLEASE SET CODE HERE";
        Account account = auth.getAccountWithCode(
                TestProperty.MastodonProperty.RedirectUrl, code);

        System.out.println(auth.getAccessToken());
        System.out.println(account.action().getUserMe().getName());
    }

    @Test
    public void testSlackAuthorizationUrl() {

        SlackAuth auth = SocialHub.getSlackAuth( //
                TestProperty.SlackProperty.ClientId, //
                TestProperty.SlackProperty.ClientSecret);

        List<SlackScope> scopes = new ArrayList<>();
        scopes.add(new SlackScope().chat().write().user());

        String scope = scopes.stream() //
                .map(SlackScope::getCode) //
                .collect(Collectors.joining(" "));

        System.out.println(auth.getAuthorizationURL(TestProperty.SlackProperty.RedirectUrl, scope));
    }

    @Test
    public void testTumblrAuthorizationUrl() {

        TumblrAuth auth = SocialHub.getTumblrAuth(
                TestProperty.TumblrProperty.ConsumerKey,
                TestProperty.TumblrProperty.ConsumerSecret);

        System.out.println(auth.getAuthorizationURL(
                TestProperty.TumblrProperty.RedirectUrl));
    }

    @Test
    @Ignore
    public void testSlackAuthorizeWithCode() {

        SlackAuth auth = SocialHub.getSlackAuth( //
                TestProperty.SlackProperty.ClientId, //
                TestProperty.SlackProperty.ClientSecret);

        String code = "PLEASE SET CODE HERE";
        Account account = auth.getAccountWithCode(TestProperty.SlackProperty.RedirectUrl, code);
        System.out.println(account.action().getUserMe().getName());
    }
}
