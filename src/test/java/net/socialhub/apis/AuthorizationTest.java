package net.socialhub.apis;

import net.socialhub.SocialHub;
import net.socialhub.TestProperty;
import net.socialhub.define.service.mastodon.MastodonInstance;
import net.socialhub.define.service.mastodon.MastodonScope;
import net.socialhub.define.service.slack.SlackScope;
import net.socialhub.model.Account;
import net.socialhub.service.mastodon.MastodonAuth;
import net.socialhub.service.slack.SlackAuth;
import net.socialhub.service.tumblr.TumblrAuth;
import net.socialhub.service.twitter.TwitterAuth;
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

        MastodonAuth auth = SocialHub.getMastodonAuth(MastodonInstance.MSTDN_JP);

        auth.requestClientApplication("SocialHub", null,
                MastodonAuth.REDIRECT_NONE,
                MastodonScope.FULL_ACCESS);

        System.out.println(auth.getClientId());
        System.out.println(auth.getClientSecret());
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
