package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.User;
import org.junit.Test;

public class UserPostTimelineTest extends AbstractTimelineTest {

    @Test
    public void testUserPostTimeline_Misskey() {
        execPast(getMisskeyAccount());
    }

    @Test
    public void testUserPostTimeline_PixelFed() {
        execPast(getPixelFedAccount());
    }

    @Test
    public void testUserPostTimeline_Tumblr() {
        execPast(getTumblrAccount());
    }

    @Test
    public void testUserPostTimeline_Bluesky() {
        execPast(getBlueskyAccount());
    }

    private void execPast(Account account) {
        Paging paging = new Paging();
        paging.setCount(10L);

        User me = account.action().getUserMe();
        Pageable<Comment> comments = account.action().getUserCommentTimeLine(me, paging);
        printTimeline("MyComments", comments);
    }
}
