package net.socialhub.apis;


import net.socialhub.SocialAuthUtil;
import net.socialhub.define.service.slack.SlackFormKey;
import net.socialhub.model.Account;
import net.socialhub.model.request.CommentForm;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;

public class PostCommentTest extends AbstractApiTest {

    @Test
    @Ignore
    public void testPostWithMediaTwitter() {

        Account account = SocialAuthUtil.getTwitterAccount();
        InputStream stream = getClass().getResourceAsStream("/image/icon.png");

        CommentForm req = new CommentForm() //
                .addImage(convertFile(stream), "icon.png") //
                .text("SocialHub Test");

        account.action().postComment(req);
    }


    @Test
    @Ignore
    public void testPostWithMediaMastodon() {

        Account account = SocialAuthUtil.getMastodonAccount();
        InputStream stream = getClass().getResourceAsStream("/image/icon.png");

        CommentForm req = new CommentForm() //
                .addImage(convertFile(stream), "icon.png") //
                .text("SocialHub Test");

        account.action().postComment(req);
    }

    @Test
    @Ignore
    public void testPostWithMediaMisskey() {

        Account account = SocialAuthUtil.getMisskeyAccount();
        InputStream stream = getClass().getResourceAsStream("/image/icon.png");

        CommentForm req = new CommentForm() //
                .addImage(convertFile(stream), "icon.png") //
                .text("SocialHub Test");

        account.action().postComment(req);
    }

    @Test
    @Ignore
    public void testPostWithMediaTumblr() {
        // System.setProperty("javax.net.debug","all");

        Account account = SocialAuthUtil.getTumblrAccount();
        InputStream stream = getClass().getResourceAsStream("/image/icon.png");

        CommentForm req = new CommentForm() //
                .addImage(convertFile(stream), "icon.png") //
                .text("SocialHub Test");

        account.action().postComment(req);
    }

    @Test
    @Ignore
    public void testPostWithMediaSlack() {
        Account account = SocialAuthUtil.getSlackAccount();
        InputStream stream = getClass().getResourceAsStream("/image/icon.png");

        CommentForm req = new CommentForm() //
                .addImage(convertFile(stream), "icon.png") //
                .param(SlackFormKey.CHANNEL_KEY, "CHANNEL_ID") //
                .text("SocialHub Test");

        account.action().postComment(req);
    }

    @Test
    @Ignore
    public void testPostHomeSlackFromRequest() {
        Account account = SocialAuthUtil.getSlackAccount();
        CommentForm req = account.request().getHomeTimeLine()
                .getCommentFrom().text("SocialHub Test");

        account.action().postComment(req);
    }
}
