package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.User;
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
        Comment comment = account.action().getComment(
                "https://mastodon.social/web/statuses/104681506368424218");
        System.out.println(comment.getDisplayComment().getText().getDisplayText());
    }
}
