package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.common.AttributedFiled;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.MiniBlogUser;
import net.socialhub.model.service.addition.mastodon.MastodonUser;
import net.socialhub.model.service.addition.misskey.MisskeyUser;
import net.socialhub.model.service.addition.tumblr.TumblrUser;
import org.junit.Ignore;
import org.junit.Test;

public class GetUserMeTest extends AbstractApiTest {

    @Test
    public void testGetUserMeTwitter() {

        Account account = SocialAuthUtil.getTwitterAccount();
        User user = account.action().getUserMe();
        System.out.println(user.getName());
        System.out.println(((MiniBlogUser) user).getWebUrl());
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
        System.out.println(((MiniBlogUser) user).getWebUrl());

        if (user instanceof MastodonUser) {
            MastodonUser mastodonUser = (MastodonUser) user;
            for (AttributedFiled filed : mastodonUser.getFields()) {
                System.out.println(filed.getName() + ":" + filed.getValue());
            }
        }
    }

    @Test
    public void testGetUserMisskey() {

        Account account = SocialAuthUtil.getMisskeyAccount();
        User user = account.action().getUserMe();
        System.out.println(user.getName());
        System.out.println(((MiniBlogUser) user).getWebUrl());

        if (user instanceof MisskeyUser) {
            MisskeyUser misskeyUser = (MisskeyUser) user;
            for (AttributedFiled filed : misskeyUser.getFields()) {
                System.out.println(filed.getName() + ":" + filed.getValue());
            }
            if (misskeyUser.getAvatarColor() != null) {
                System.out.println("RGB:" + misskeyUser.getAvatarColor().toJavaScriptFormat());
            }
        }
    }

    @Test
    public void testGetUserMeTumblr() {
        // System.setProperty("javax.net.debug","all");

        Account account = SocialAuthUtil.getTumblrAccount();
        User user = account.action().getUserMe();
        System.out.println(user.getName());
        System.out.println(user.getCoverImageUrl());

        if (user instanceof TumblrUser) {
            System.out.println(((TumblrUser) user).getBlogUrl());
        }
    }

    @Test
    public void testGetUserSlack() {

        Account account = SocialAuthUtil.getSlackAccount();
        User user = account.action().getUserMe();
        System.out.println(user.getName());
    }
}
