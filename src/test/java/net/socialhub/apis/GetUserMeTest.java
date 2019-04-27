package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.common.AttributedFiled;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.mastodon.MastodonUser;
import org.junit.Ignore;
import org.junit.Test;

public class GetUserMeTest extends AbstractApiTest {

    @Test
    public void testGetUserMeTwitter() {

        Account account = SocialAuthUtil.getTwitterAccount();
        User user = account.action().getUserMe();
        System.out.println(user.getName());
    }

    @Test
    @Ignore
    public void testGetUserMeFacebook() {

        Account account = SocialAuthUtil.getFacebookAccount();
        User user = account.action().getUserMe();
        System.out.println(user.getName());
    }

    @Test
    public void testGetUserMastodon() {

        Account account = SocialAuthUtil.getMastodonAccount();
        User user = account.action().getUserMe();
        System.out.println(user.getName());

        if (user instanceof MastodonUser) {
            MastodonUser mastodonUser = (MastodonUser) user;
            for (AttributedFiled filed : mastodonUser.getFields()) {
                System.out.println(filed.getName() + ":" + filed.getValue());
            }
        }
    }

    @Test
    public void testGetUserSlack() {

        Account account = SocialAuthUtil.getSlackAccount();
        User user = account.action().getUserMe();
        System.out.println(user.getName());
    }
}
