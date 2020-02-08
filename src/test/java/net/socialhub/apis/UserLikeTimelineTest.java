package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import org.junit.Test;

public class UserLikeTimelineTest extends AbstractTimelineTest {

    @Test
    public void testUserLikeTimelineTumblr() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getTumblrAccount();
        Identify identify = new Identify(account.getService());
        identify.setId("URL");

        Pageable<Comment> comments = account.action().getUserLikeTimeLine(identify, paging);
        printTimeline("Likes", comments);
    }
}
