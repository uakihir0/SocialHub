package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Pageable;
import net.socialhub.core.model.service.Paging;
import net.socialhub.core.model.service.User;
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
