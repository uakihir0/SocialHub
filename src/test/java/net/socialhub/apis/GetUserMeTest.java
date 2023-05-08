package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.common.AttributedFiled;
import net.socialhub.core.model.User;
import net.socialhub.service.mastodon.model.MastodonUser;
import net.socialhub.service.misskey.model.MisskeyUser;
import net.socialhub.service.tumblr.model.TumblrUser;
import org.junit.Test;

public class GetUserMeTest extends AbstractApiTest {

    @Test
    public void testGetUserMeTwitter() {
        execUser(SocialAuthUtil.getTwitterAccount());
    }

    @Test
    public void testGetUserMastodon() {
        execUser(SocialAuthUtil.getMastodonAccount());
    }

    @Test
    public void testGetUserPixelFed() {
        execUser(SocialAuthUtil.getPixelFedAccount());
    }

    @Test
    public void testGetUserPleroma() {
        execUser(SocialAuthUtil.getPleromaAccount());
    }

    @Test
    public void testGetUserMisskey() {
        execUser(SocialAuthUtil.getMisskeyAccount());
    }

    @Test
    public void testGetUserMeTumblr() {
        // System.setProperty("javax.net.debug","all");
        execUser(SocialAuthUtil.getTumblrAccount());
    }

    @Test
    public void testGetUserSlack() {
        execUser(SocialAuthUtil.getSlackAccount());
    }

    private void execUser(Account account) {

        User user = account.action().getUserMe();
        System.out.println("Name > " + user.getName());
        System.out.println("Url > " + user.getWebUrl());
        System.out.println("Icon > " + user.getIconImageUrl());

        if (user instanceof MastodonUser) {
            MastodonUser mastodonUser = (MastodonUser) user;
            for (AttributedFiled filed : mastodonUser.getFields()) {
                System.out.println(filed.getName() + ":" + filed.getValue());
            }
        }

        if (user instanceof MisskeyUser) {
            MisskeyUser misskeyUser = (MisskeyUser) user;
            for (AttributedFiled filed : misskeyUser.getFields()) {
                System.out.println(filed.getName() + ":" + filed.getValue().getDisplayText());
            }
            if (misskeyUser.getAvatarColor() != null) {
                System.out.println("RGB:" + misskeyUser.getAvatarColor().toJavaScriptFormat());
            }
        }

        if (user instanceof TumblrUser) {
            System.out.println(((TumblrUser) user).getBlogUrl());
        }
    }
}
