package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Context;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.addition.slack.SlackIdentify;
import org.junit.Ignore;
import org.junit.Test;

public class GetContextTest extends AbstractApiTest {

    @Test
    @Ignore
    public void testTwitterGetContext() {
        Long id = 0L;
        Identify identify = new Identify(null, id);

        Account account = SocialAuthUtil.getTwitterAccount();
        Context context = account.action().getCommentContext(identify);
        printContext(context);
    }

    @Test
    @Ignore
    public void testMastodonGetContext() {
        Long id = 0L;
        Identify identify = new Identify(null, id);

        Account account = SocialAuthUtil.getMastodonAccount();
        Context context = account.action().getCommentContext(identify);
        printContext(context);
    }

    @Test
    @Ignore
    public void testMisskeyGetContext() {
        String id = "";
        Identify identify = new Identify(null, id);

        Account account = SocialAuthUtil.getMisskeyAccount();
        Context context = account.action().getCommentContext(identify);
        printContext(context);
    }

    @Test
    @Ignore
    public void testSlackGetContext() {
        String id = "";

        SlackIdentify identify = new SlackIdentify(null, id);
        identify.setChannel("");

        Account account = SocialAuthUtil.getSlackAccount();
        Context context = account.action().getCommentContext(identify);
        printContext(context);
    }

    private void printContext(Context context) {

        System.out.println("========================");
        System.out.println("> Before");
        System.out.println("========================");
        context.getAncestors().forEach(this::printComment);

        System.out.println("========================");
        System.out.println("> After");
        System.out.println("========================");
        context.getDescendants().forEach(this::printComment);
    }

    private void printComment(Comment comment){
        System.out.println(">ID: " + comment.getId());
        Comment display = comment.getDisplayComment();
        System.out.println(">Text: " + display.getText().getDisplayText());
        System.out.println(">Date: " + display.getCreateAt());
    }
}
