package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Media;
import net.socialhub.core.model.Stream;
import net.socialhub.core.model.event.CommentEvent;
import net.socialhub.core.model.event.IdentifyEvent;
import net.socialhub.core.action.callback.comment.DeleteCommentCallback;
import net.socialhub.core.action.callback.comment.UpdateCommentCallback;
import net.socialhub.core.action.callback.lifecycle.DisconnectCallback;
import net.socialhub.service.mastodon.action.MastodonAction;
import net.socialhub.service.misskey.action.MisskeyAction;
import net.socialhub.service.twitter.action.TwitterAction;
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
    public void testFederationTimeLineStream_Misskey() throws Exception {

        Account account = SocialAuthUtil.getMisskeyAccount();
        Stream stream = ((MisskeyAction) account.action())
                .setFederationLineStream(new StreamCallback());

        stream.open();
        Thread.sleep(1000 * 100);
        stream.close();
    }

    @Test
    public void testHomeTimeLineStream_Misskey() throws Exception {

        Account account = SocialAuthUtil.getMisskeyAccount();
        Stream stream = ((MisskeyAction) account.action())
                .setHomeTimeLineStream(new StreamCallback());

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

    static class StreamCallback implements UpdateCommentCallback, DeleteCommentCallback, DisconnectCallback {

        @Override
        public void onDelete(IdentifyEvent event) {
            System.out.println("Delete> " + event.getId());
        }

        @Override
        public void onUpdate(CommentEvent event) {
            printComment(event.getComment());
        }

        @Override
        public void onDisconnect() {
            System.out.println("!!onDisconnect!!");
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