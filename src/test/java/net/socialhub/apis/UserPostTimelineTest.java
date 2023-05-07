package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.User;
import org.junit.Test;

public class UserPostTimelineTest extends AbstractTimelineTest {

    @Test
    public void testUserPostTimelineMisskey() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getMisskeyAccount();
        User me = account.action().getUserMe();

        Pageable<Comment> comments = account.action().getUserCommentTimeLine(me, paging);
        printTimeline("MyComment:", comments);
    }

    @Test
    public void testUserPostTimelinePixelFed() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getPixelFedAccount();
        User me = account.action().getUserMe();

        Pageable<Comment> comments = account.action().getUserCommentTimeLine(me, paging);
        printTimeline("MyComment:", comments);
    }

    @Test
    public void testUserPostTimelineTumblr() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getTumblrAccount();
        Identify identify = new Identify(account.getService());
        identify.setId("URL");

        Pageable<Comment> comments = account.action().getUserCommentTimeLine(identify, paging);
        printTimeline("Likes", comments);
    }
}
