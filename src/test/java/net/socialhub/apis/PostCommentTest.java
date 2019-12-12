package net.socialhub.apis;


import net.socialhub.SocialAuthUtil;
import net.socialhub.define.service.slack.SlackFormKey;
import net.socialhub.model.Account;
import net.socialhub.model.request.CommentForm;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
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
                .getCommentRequest().text("SocialHub Test");

        account.action().postComment(req);
    }

    /**
     * File to ImageBytes
     */
    private static byte[] convertFile(InputStream stream) {
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {

            int len = 0;
            byte[] buffer = new byte[1024];

            while ((len = stream.read(buffer)) != -1) {
                bout.write(buffer, 0, len);
            }
            return bout.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
