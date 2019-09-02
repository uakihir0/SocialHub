package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Stream;
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
            System.out.println("Update> " + event.getComment()
                    .getDisplayComment().getText().getDisplayText());
        }
    }

}