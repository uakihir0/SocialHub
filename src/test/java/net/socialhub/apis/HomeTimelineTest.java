package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.slack.SlackComment;
import net.socialhub.model.service.paging.BorderPaging;
import org.junit.Test;

public class HomeTimelineTest extends AbstractApiTest {

    @Test
    public void testHomeTimelineTwitter_New() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getTwitterAccount();

        Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
        Pageable<Comment> news = account.action().getHomeTimeLine(comments.newPage());

        printTimeline("New", news);
        printTimeline("Now", comments);
    }

    @Test
    public void testHomeTimelineTwitter_Past() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getTwitterAccount();

        Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
        Pageable<Comment> pasts = account.action().getHomeTimeLine(comments.pastPage());

        printTimeline("Now", comments);
        printTimeline("Past", pasts);
    }

    @Test
    public void testHomeTimelineMastodon_New() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getMastodonAccount();

        Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
        Pageable<Comment> news = account.action().getHomeTimeLine(comments.newPage());

        printTimeline("New", news);
        printTimeline("Now", comments);
    }

    @Test
    public void testHomeTimelineMastodon_Past() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getMastodonAccount();

        Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
        Pageable<Comment> pasts = account.action().getHomeTimeLine(comments.pastPage());

        printTimeline("Now", comments);
        printTimeline("Past", pasts);
    }

    @Test
    public void testHomeTimelineSlack_New() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getSlackAccount();

        Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
        Pageable<Comment> news = account.action().getHomeTimeLine(comments.newPage());

        printTimeline("New", news);
        printTimeline("Now", comments);
    }

    @Test
    public void testHomeTimelineSlack_Past() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getSlackAccount();

        Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
        Pageable<Comment> pasts = account.action().getHomeTimeLine(comments.pastPage());

        printTimeline("Now", comments);
        printTimeline("Past", pasts);
    }

    @Test
    public void testHomeTimelineTumblr_New() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getTumblrAccount();

        Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
        Pageable<Comment> news = account.action().getHomeTimeLine(comments.newPage());

        printTimeline("New", news);
        printTimeline("Now", comments);
    }

    @Test
    public void testHomeTimelineTumblr_Past() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getTumblrAccount();

        Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
        Pageable<Comment> pasts = account.action().getHomeTimeLine(comments.pastPage());

        printTimeline("Now", comments);
        printTimeline("Past", pasts);
    }
}
