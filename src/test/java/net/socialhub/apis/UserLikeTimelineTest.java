package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.User;
import org.junit.Test;

public class UserLikeTimelineTest extends AbstractTimelineTest {


    @Test
    public void testUserLikeTimelineMastodon() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = getMastodonAccount();
        User me = account.action().getUserMe();

        Pageable<Comment> comments = account.action().getUserLikeTimeLine(me, paging);
        printTimeline("Likes", comments);
    }

    @Test
    public void testUserLikeTimelineTumblr() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = getTumblrAccount();
        User me = account.action().getUserMe();

        Pageable<Comment> comments = account.action().getUserLikeTimeLine(me, paging);
        printTimeline("Likes", comments);
    }
}
