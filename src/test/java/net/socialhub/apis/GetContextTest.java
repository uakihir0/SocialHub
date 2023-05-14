package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Context;
import net.socialhub.core.model.Identify;
import net.socialhub.service.slack.model.SlackIdentify;
import org.junit.Ignore;
import org.junit.Test;

public class GetContextTest extends AbstractApiTest {

    @Test
    @Ignore
    public void testTwitterGetContext() {
        Long id = 0L;
        Identify identify = new Identify(null, id);

        Account account = getTwitterAccount();
        Context context = account.action().getCommentContext(identify);
        printContext(context);
    }

    @Test
    @Ignore
    public void testMastodonGetContext() {
        Long id = 0L;
        Identify identify = new Identify(null, id);

        Account account = getMastodonAccount();
        Context context = account.action().getCommentContext(identify);
        printContext(context);
    }

    @Test
    @Ignore
    public void testMisskeyGetContext() {
        String id = "";
        Identify identify = new Identify(null, id);

        Account account = getMisskeyAccount();
        Context context = account.action().getCommentContext(identify);
        printContext(context);
    }

    @Test
    @Ignore
    public void testSlackGetContext() {
        String id = "";

        SlackIdentify identify = new SlackIdentify(null, id);
        identify.setChannel("");

        Account account = getSlackAccount();
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

    private void printComment(Comment comment) {
        System.out.println(">ID: " + comment.getId());
        Comment display = comment.getDisplayComment();
        System.out.println(">Text: " + display.getText().getDisplayText());
        System.out.println(">Date: " + display.getCreateAt());
    }
}
