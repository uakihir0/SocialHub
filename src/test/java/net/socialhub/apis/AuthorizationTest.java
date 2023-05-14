package net.socialhub.apis;

import net.socialhub.core.SocialHub;
import net.socialhub.core.model.Account;
import net.socialhub.service.mastodon.action.MastodonAuth;
import net.socialhub.service.mastodon.define.MastodonScope;
import net.socialhub.service.slack.action.SlackAuth;
import net.socialhub.service.slack.define.SlackScope;
import net.socialhub.service.tumblr.action.TumblrAuth;
import net.socialhub.service.twitter.action.TwitterAuth;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuthorizationTest extends AbstractApiTest {

    @Test
    public void testTwitterAuthorizationUrl() {

        TwitterAuth auth = SocialHub.getTwitterAuth(
                twitterProperty.getClientId(),
                twitterProperty.getClientSecret());

        System.out.println(auth.getAuthorizationURL(
                twitterProperty.getRedirectUri()));
    }

    @Test
    public void testMastodonAppRegister() {

        MastodonAuth auth = SocialHub.getMastodonAuth(
                mastodonProperty.getHost());

        auth.requestClientApplication("SocialHub", null,
                mastodonProperty.getRedirectUri(),
                MastodonScope.FULL_ACCESS);

        System.out.println(auth.getClientId());
        System.out.println(auth.getClientSecret());
    }

    @Test
    public void testMastodonAuthorizationUrl() {

        MastodonAuth auth = SocialHub.getMastodonAuth(
                mastodonProperty.getHost());

        auth.setClientInfo(
                mastodonProperty.getClientId(),
                mastodonProperty.getClientSecret());

        System.out.println(auth.getAuthorizationURL(
                mastodonProperty.getRedirectUri(),
                MastodonScope.FULL_ACCESS
        ));
    }


    @Test
    public void testMastodonAuthorizeWithCode() {

        MastodonAuth auth = SocialHub.getMastodonAuth(
                mastodonProperty.getHost());

        auth.setClientInfo(
                mastodonProperty.getClientId(),
                mastodonProperty.getClientSecret());

        String code = "PLEASE SET CODE HERE";
        Account account = auth.getAccountWithCode(
                mastodonProperty.getRedirectUri(), code);

        System.out.println(auth.getAccessToken());
        System.out.println(account.action().getUserMe().getName());
    }

    @Test
    public void testSlackAuthorizationUrl() {

        SlackAuth auth = SocialHub.getSlackAuth(
                slackProperty.getClientId(),
                slackProperty.getClientSecret());

        List<SlackScope> scopes = new ArrayList<>();
        scopes.add(new SlackScope().chat().write().user());

        String scope = scopes.stream()
                .map(SlackScope::getCode)
                .collect(Collectors.joining(" "));

        System.out.println(auth.getAuthorizationURL(
                slackProperty.getRedirectUri(), scope));
    }

    @Test
    public void testTumblrAuthorizationUrl() {

        TumblrAuth auth = SocialHub.getTumblrAuth(
                twitterProperty.getClientId(),
                twitterProperty.getClientSecret());

        System.out.println(auth.getAuthorizationURL(
                tumblrProperty.getRedirectUri()));
    }

    @Test
    @Ignore
    public void testSlackAuthorizeWithCode() {

        SlackAuth auth = SocialHub.getSlackAuth(
                slackProperty.getClientId(),
                slackProperty.getClientSecret());

        String code = "PLEASE SET CODE HERE";
        Account account = auth.getAccountWithCode(
                slackProperty.getRedirectUri(), code);
        System.out.println(account.action().getUserMe().getName());
    }
}
