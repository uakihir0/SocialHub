package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.request.CommentForm;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;

public class PostMessageTest extends AbstractApiTest {

    @Test
    @Ignore
    public void testPostWithMediaTwitter() {

        Account account = SocialAuthUtil.getTwitterAccount();
        InputStream stream = getClass().getResourceAsStream("/image/icon.png");

        CommentForm req = new CommentForm()
                .addImage(convertFile(stream), "icon.png")
                .text("SocialHub Test")
                .targetId(0L)
                .message(true);

        account.action().postMessage(req);
    }

    @Test
    @Ignore
    public void testPostWithMediaSlack() {

        Account account = SocialAuthUtil.getSlackAccount();
        InputStream stream = getClass().getResourceAsStream("/image/icon.png");

        CommentForm req = new CommentForm()
                .addImage(convertFile(stream), "icon.png")
                .text("SocialHub Test")
                .targetId("")
                .message(true);

        account.action().postMessage(req);
    }

}
