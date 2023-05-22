package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.User;
import org.junit.Test;

public class UserLikeTimelineTest extends AbstractTimelineTest {

    @Test
    public void testUserLikeTimelinePast_Mastodon() {
        execPast(getMastodonAccount());
    }

    @Test
    public void testUserLikeTimelinePast_Tumblr() {
        execPast(getTumblrAccount());
    }

    @Test
    public void testUserLikeTimelinePast_Bluesky() {
        execPast(getBlueskyAccount());
    }

    @Test
    public void testUserLikeTimelineNew_Bluesky() {
        execNew(getBlueskyAccount());
    }

    private void execNew(Account account) {
        Paging paging = new Paging();
        paging.setCount(10L);

        User me = account.action().getUserMe();
        Pageable<Comment> comments = account.action().getUserLikeTimeLine(me, paging);
        Pageable<Comment> news = account.action().getUserLikeTimeLine(me, comments.newPage());

        printTimeline("Likes New", news);
        printTimeline("Likes Now", comments);
    }

    private void execPast(Account account) {
        Paging paging = new Paging();
        paging.setCount(10L);

        User me = account.action().getUserMe();
        Pageable<Comment> comments = account.action().getUserLikeTimeLine(me, paging);
        Pageable<Comment> pasts = account.action().getUserLikeTimeLine(me, comments.pastPage());

        printTimeline("Likes Now", comments);
        printTimeline("Likes Past", pasts);
    }
}
