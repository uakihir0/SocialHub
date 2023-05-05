package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Media;
import net.socialhub.core.model.service.Stream;
import net.socialhub.core.model.service.User;
import net.socialhub.core.model.service.event.CommentEvent;
import net.socialhub.core.model.service.event.NotificationEvent;
import net.socialhub.core.model.service.event.UserEvent;
import net.socialhub.core.action.callback.comment.MentionCommentCallback;
import net.socialhub.core.action.callback.comment.NotificationCommentCallback;
import net.socialhub.core.action.callback.user.FollowUserCallback;
import org.junit.Test;

public class NotificationStreamTest extends AbstractApiTest {

    @Test
    public void testNotificationStream_Twitter() throws Exception {

        Account account = SocialAuthUtil.getTwitterAccount();
        Stream stream = account.action().setNotificationStream(new StreamCallback());

        stream.open();
        Thread.sleep(1000 * 1000);
        stream.close();
    }

    @Test
    public void testNotificationStream_Misskey() throws Exception {

        Account account = SocialAuthUtil.getMisskeyAccount();
        Stream stream = account.action().setNotificationStream(new StreamCallback());

        stream.open();
        Thread.sleep(1000 * 1000);
        stream.close();
    }

    @Test
    public void testNotificationStream_Mastodon() throws Exception {

        Account account = SocialAuthUtil.getMastodonAccount();
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