package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Context;
import net.socialhub.core.model.Identify;
import net.socialhub.service.slack.model.SlackIdentify;
import org.junit.Test;

public class GetContextTest extends AbstractApiTest {

    @Test
    public void testGetContext_Twitter() {
        Long id = 0L;

        Account account = getTwitterAccount();
        Identify identify = new Identify(account.getService(), id);

        Context context = account.action().getCommentContext(identify);
        printContext(context);
    }

    @Test
    public void testGetContext_Mastodon() {
        Long id = 0L;

        Account account = getMastodonAccount();
        Identify identify = new Identify(account.getService(), id);

        Context context = account.action().getCommentContext(identify);
        printContext(context);
    }

    @Test
    public void testGetContext_Misskey() {
        String id = "";

        Account account = getMisskeyAccount();
        Identify identify = new Identify(account.getService(), id);

        Context context = account.action().getCommentContext(identify);
        printContext(context);
    }

    @Test
    public void testGetContext_Slack() {
        String id = "";

        Account account = getSlackAccount();
        SlackIdentify identify = new SlackIdentify(account.getService(), id);
        identify.setChannel("");

        Context context = account.action().getCommentContext(identify);
        printContext(context);
    }

    @Test
    public void testGetContext_Bluesky() {
        String uri = "";

        Account account = getBlueskyAccount();
        Identify identify = new Identify(account.getService(), uri);

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
