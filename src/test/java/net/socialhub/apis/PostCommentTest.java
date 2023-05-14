package net.socialhub.apis;


import net.socialhub.core.model.Account;
import net.socialhub.core.model.request.CommentForm;
import net.socialhub.core.model.request.PollForm;
import net.socialhub.service.slack.define.SlackFormKey;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;

public class PostCommentTest extends AbstractApiTest {

    @Test
    @Ignore
    public void testPostWithMediaTwitter() {

        Account account = getTwitterAccount();
        InputStream stream = getClass().getResourceAsStream("/image/icon.png");

        CommentForm req = new CommentForm() //
                .addImage(convertFile(stream), "icon.png") //
                .text("SocialHub Test");

        account.action().postComment(req);
    }

    @Test
    @Ignore
    public void testPostWithMediaMastodon() {

        Account account = getMastodonAccount();
        InputStream stream = getClass().getResourceAsStream("/image/icon.png");

        CommentForm req = new CommentForm() //
                .addImage(convertFile(stream), "icon.png") //
                .text("SocialHub Test");

        account.action().postComment(req);
    }

    @Test
    @Ignore
    public void testPostWithMediaPixelFed() {

        Account account = getPixelFedAccount();
        InputStream stream = getClass().getResourceAsStream("/image/icon.png");

        CommentForm req = new CommentForm() //
                .addImage(convertFile(stream), "icon.png") //
                .text("SocialHub Test");

        account.action().postComment(req);
    }

    @Test
    @Ignore
    public void testPostWithMediaMisskey() {

        Account account = getMisskeyAccount();
        InputStream stream = getClass().getResourceAsStream("/image/icon.png");

        CommentForm req = new CommentForm() //
                .addImage(convertFile(stream), "icon.png") //
                .text("SocialHub Test");

        account.action().postComment(req);
    }

    @Test
    @Ignore
    public void testPostWithPollMisskey() {

        Account account = getMisskeyAccount();

        CommentForm req = new CommentForm() //
                .text("SocialHub Test")
                .poll(new PollForm()
                        .multiple(true)
                        .addOption("A")
                        .addOption("B")
                        .addOption("C")
                        .expiresIn(1440L));

        account.action().postComment(req);
    }

    @Test
    @Ignore
    public void testPostWithMediaTumblr() {
        // System.setProperty("javax.net.debug","all");

        Account account = getTumblrAccount();
        InputStream stream = getClass().getResourceAsStream("/image/icon.png");

        CommentForm req = new CommentForm() //
                .addImage(convertFile(stream), "icon.png") //
                .text("SocialHub Test");

        account.action().postComment(req);
    }

    @Test
    @Ignore
    public void testPostWithMediaSlack() {
        Account account = getSlackAccount();
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
        Account account = getSlackAccount();
        CommentForm req = account.request().getHomeTimeLine()
                .getCommentFrom().text("SocialHub Test");

        account.action().postComment(req);
    }
}
