package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.User;
import org.junit.Test;

public class UserLikeTimelineTest extends AbstractTimelineTest {


    @Test
    public void testUserLikeTimelineMastodon() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getMastodonAccount();
        User me = account.action().getUserMe();

        Pageable<Comment> comments = account.action().getUserLikeTimeLine(me, paging);
        printTimeline("Likes", comments);
    }

    @Test
    public void testUserLikeTimelineTumblr() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getTumblrAccount();
        User me = account.action().getUserMe();

        Pageable<Comment> comments = account.action().getUserLikeTimeLine(me, paging);
        printTimeline("Likes", comments);
    }
}
