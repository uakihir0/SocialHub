package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.User;
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
        System.out.println(user.getName());
    }

    @Test
    public void testGetUserSlack() {

        Account account = SocialAuthUtil.getSlackAccount();
        User user = account.getAction().getUserMe();
        System.out.println(user.getName());
    }
}
