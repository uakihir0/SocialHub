package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Media;
import net.socialhub.model.service.Reaction;
import net.socialhub.model.service.Stream;
import net.socialhub.model.service.addition.slack.SlackComment;
import net.socialhub.model.service.event.DeleteCommentEvent;
import net.socialhub.model.service.event.UpdateCommentEvent;
import net.socialhub.service.action.callback.DeleteCommentCallback;
import net.socialhub.service.action.callback.UpdateCommentCallback;
import net.socialhub.service.mastodon.MastodonAction;
import net.socialhub.service.twitter.TwitterAction;
import org.junit.Test;

public class CommentStreamTest extends AbstractApiTest {

    @Test
    public void testFederationTimeLineStream_Mastodon() throws Exception {

        Account account = SocialAuthUtil.getMastodonAccount();
        Stream stream = ((MastodonAction) account.action())
                .setFederationLineStream(new StreamCallback());

        stream.open();
        Thread.sleep(1000 * 10);
        stream.close();
    }

    @Test
    public void testSearchTimeLineStream_Twitter() throws Exception {

        Account account = SocialAuthUtil.getTwitterAccount();
        Stream stream = ((TwitterAction) account.action())
                .setSearchTimeLineStream(new StreamCallback(), "#NowPlaying");

        stream.open();
        Thread.sleep(1000 * 10);
        stream.close();
    }

    @Test
    public void testHomeTimeLineStream_Twitter() throws Exception {

        Account account = SocialAuthUtil.getTwitterAccount();
        Stream stream = ((TwitterAction) account.action())
                .setHomeTimeLineStream(new StreamCallback());

        stream.open();
        Thread.sleep(1000 * 10);
        stream.close();
    }

    static class StreamCallback implements UpdateCommentCallback, DeleteCommentCallback {

        @Override
        public void onDelete(DeleteCommentEvent event) {
            System.out.println("Delete> " + event.getId());
        }

        @Override
        public void onUpdate(UpdateCommentEvent event) {
            printComment(event.getComment());
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
    }

}