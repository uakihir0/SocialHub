package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.MastodonUser;
import net.socialhub.model.service.addition.MastodonUser.MastodonUserFiled;
import org.junit.Test;

public class GetUserMeTest extends AbstractApiTest {

    @Test
    public void testGetUserMeTwitter() {

        Account account = SocialAuthUtil.getTwitterAccount();
        User user = account.getAction().getUserMe();
        System.out.println(user.getName());
    }

    @Test
    public void testGetUserMeFacebook() {

        Account account = SocialAuthUtil.getFacebookAccount();
        User user = account.getAction().getUserMe();
        System.out.println(user.getName());
    }

    @Test
    public void testGetUserMastodon() {

        Account account = SocialAuthUtil.getMastodonAccount();
        User user = account.getAction().getUserMe();

        if (user instanceof MastodonUser) {
            MastodonUser mastodonUser = (MastodonUser) user;
            for (MastodonUserFiled filed : mastodonUser.getFields()) {
                System.out.println(filed.getName() + ":" + filed.getValue());
            }
        }
    }

    @Test
    public void testGetUserSlack() {

        Account account = SocialAuthUtil.getSlackAccount();
        User user = account.getAction().getUserMe();
        System.out.println(user.getName());
    }
}
