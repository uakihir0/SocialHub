package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.User;
import net.socialhub.core.model.common.AttributedFiled;
import net.socialhub.service.mastodon.model.MastodonUser;
import net.socialhub.service.misskey.model.MisskeyUser;
import net.socialhub.service.tumblr.model.TumblrUser;
import org.junit.Test;

public class GetUserMeTest extends AbstractApiTest {

    @Test
    public void testGetUserMe_Twitter() {
        execUser(getTwitterAccount());
    }

    @Test
    public void testGetUserMe_Mastodon() {
        execUser(getMastodonAccount());
    }

    @Test
    public void testGetUserMe_PixelFed() {
        execUser(getPixelFedAccount());
    }

    @Test
    public void testGetUserMe_Pleroma() {
        execUser(getPleromaAccount());
    }

    @Test
    public void testGetUserMe_Misskey() {
        execUser(getMisskeyAccount());
    }

    @Test
    public void testHetUserMe_Bluesky() {
        execUser(getBlueskyAccount());
    }

    @Test
    public void testGetUserMeTumblr() {
        // System.setProperty("javax.net.debug","all");
        execUser(getTumblrAccount());
    }

    @Test
    public void testGetUserSlack() {
        execUser(getSlackAccount());
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
