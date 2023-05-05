package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.User;
import org.junit.Test;

public class GetFromUrlTest extends AbstractApiTest {

    @Test
    public void getUserFromUrlTwitter() {
        Account account = SocialAuthUtil.getTwitterAccount();
        User user = account.action().getUser(
                "https://twitter.com/uakihir0");
        System.out.println(user.getName());
    }

    @Test
    public void getTweetFromUrlTwitter() {
        Account account = SocialAuthUtil.getTwitterAccount();
        Comment comment = account.action().getComment(
                "https://twitter.com/uakihir0/status/1232157896453963776");
        System.out.println(comment.getDisplayComment().getText().getDisplayText());
    }

    @Test
    public void getUserFromUrlMastodon() {
        Account account = SocialAuthUtil.getMastodonAccount();
        {
            User user = account.action().getUser("https://mastodon.social/@uakihir0");
            System.out.println(user.getName());
        }
        {
            User user = account.action().getUser("https://mastodon.social/web/accounts/1223371");
            System.out.println(user.getName());
        }
    }

    @Test
    public void getCommentFromUrlMastodon() {
        Account account = SocialAuthUtil.getMastodonAccount();
        {
            Comment comment = account.action().getComment(
                    "https://mastodon.social/@uakihir0/104681506368424218");
            System.out.println(comment.getDisplayComment().getText().getDisplayText());
        }
        {
            Comment comment = account.action().getComment(
                    "https://mastodon.social/web/statuses/104681506368424218");
            System.out.println(comment.getDisplayComment().getText().getDisplayText());
        }
    }

    @Test
    public void getUserFromUrlMisskey() {
        Account account = SocialAuthUtil.getMisskeyAccount();
        {
            User user = account.action().getUser("https://misskey.io/@syuilo");
            System.out.println(user.getName());
        }
        {
            User user = account.action().getUser("https://misskey.io/@mei23@misskey.m544.net");
            System.out.println(user.getName());
        }
    }

    @Test
    public void getCommentFromUrlMisskey() {
        Account account = SocialAuthUtil.getMisskeyAccount();
        Comment comment = account.action().getComment(
                "https://misskey.io/notes/8axwbcxiff");
        System.out.println(comment.getDisplayComment().getText().getDisplayText());
    }
}
