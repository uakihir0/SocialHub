package net.socialhub.apis;

import net.socialhub.core.action.callback.comment.MentionCommentCallback;
import net.socialhub.core.action.callback.comment.NotificationCommentCallback;
import net.socialhub.core.action.callback.user.FollowUserCallback;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Media;
import net.socialhub.core.model.Stream;
import net.socialhub.core.model.User;
import net.socialhub.core.model.event.CommentEvent;
import net.socialhub.core.model.event.NotificationEvent;
import net.socialhub.core.model.event.UserEvent;
import org.junit.Test;

public class NotificationStreamTest extends AbstractApiTest {

    @Test
    public void testNotificationStream_Twitter() throws Exception {

        Account account = getTwitterAccount();
        Stream stream = account.action().setNotificationStream(new StreamCallback());

        stream.open();
        Thread.sleep(1000 * 1000);
        stream.close();
    }

    @Test
    public void testNotificationStream_Misskey() throws Exception {

        Account account = getMisskeyAccount();
        Stream stream = account.action().setNotificationStream(new StreamCallback());

        stream.open();
        Thread.sleep(1000 * 1000);
        stream.close();
    }

    @Test
    public void testNotificationStream_Mastodon() throws Exception {

        Account account = getMastodonAccount();
        Stream stream = account.action().setNotificationStream(new StreamCallback());

        stream.open();
        Thread.sleep(1000 * 1000);
        stream.close();
    }

    static class StreamCallback implements
            NotificationCommentCallback,
            MentionCommentCallback,
            FollowUserCallback {

        @Override
        public void onMention(CommentEvent event) {
            System.out.println("// ---------------------------------------------------- //");
            System.out.println("onMention");
            printComment(event.getComment());
        }

        @Override
        public void onNotification(NotificationEvent reaction) {
            System.out.println("// ---------------------------------------------------- //");
            System.out.println("onNotification: " + reaction.getNotification().getType());
        }

        @Override
        public void onFollow(UserEvent event) {
            System.out.println("// ---------------------------------------------------- //");
            System.out.println("onFollow");
            printUser(event.getUser());
        }

        protected void printComment(Comment c) {
            System.out.println("// ---------------------------------------------------- //");
            System.out.println("" + c.getId() + " : " + c.getCreateAt());
            System.out.println("" + c.getUser().getName());

            Comment dc = c.getDisplayComment();
            System.out.println("Text > " + dc.getText().getDisplayText());

            for (Media m : dc.getMedias()) {
                System.out.println("Media > " + m.getType());
                System.out.println("M Source > " + m.getSourceUrl());
                System.out.println("M Preview > " + m.getPreviewUrl());
            }
        }

        protected void printUser(User c) {
            System.out.println("// ---------------------------------------------------- //");
            System.out.println("ID > " + c.getId());
            System.out.println("Name > " + c.getName());
        }
    }
}