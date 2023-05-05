package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.request.CommentForm;
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
                .replyId(0L)
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
                .replyId("")
                .message(true);

        account.action().postMessage(req);
    }

}
