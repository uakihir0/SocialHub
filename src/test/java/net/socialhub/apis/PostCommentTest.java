package net.socialhub.apis;


import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.request.CommentRequest;
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

        CommentRequest req = new CommentRequest() //
                .addImage(convertFile(stream), "icon.png") //
                .message("SocialHub Test");

        account.action().postComment(req);
    }


    @Test
    @Ignore
    public void testPostWithMediaMastodon() {

        Account account = SocialAuthUtil.getMastodonAccount();
        InputStream stream = getClass().getResourceAsStream("/image/icon.png");

        CommentRequest req = new CommentRequest() //
                .addImage(convertFile(stream), "icon.png") //
                .message("SocialHub Test");

        account.action().postComment(req);
    }


    @Test
    @Ignore
    public void testPostWithMediaTumblr() {
        // System.setProperty("javax.net.debug","all");

        Account account = SocialAuthUtil.getTumblrAccount();
        InputStream stream = getClass().getResourceAsStream("/image/icon.png");

        CommentRequest req = new CommentRequest() //
                .addImage(convertFile(stream), "icon.png") //
                .message("SocialHub Test");

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
