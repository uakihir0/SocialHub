package net.socialhub.apis;

import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.SocialAuthUtil;
import org.junit.Test;

public class HomeTimelineTest extends AbstractApiTest {

    @Test
    public void testHomeTimelineTwitter() {

        Paging paging = new Paging();
        paging.setCount(100L);

        Account account = SocialAuthUtil.getTwitterAccount();
        Pageable<Comment> comments = account.getAction().getHomeTimeLine(paging);

        System.out.println(comments.getEntities().get(0).getComment());
        System.out.println(comments.getEntities().get(0).getUser().getName());
    }


    @Test
    public void testHomeTimelineMastodon() {

        Paging paging = new Paging();
        paging.setCount(100L);

        Account account = SocialAuthUtil.getMastodonAccount();
        Pageable<Comment> comments = account.getAction().getHomeTimeLine(paging);

        System.out.println(comments.getEntities().get(0).getComment());
        System.out.println(comments.getEntities().get(0).getUser().getName());
    }

}
