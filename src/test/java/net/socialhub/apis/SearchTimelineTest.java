package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import org.junit.Test;

public class SearchTimelineTest extends AbstractApiTest {

    @Test
    public void testUserMediaTimelineTwitter() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getTwitterAccount();
        Pageable<Comment> comments = account.action().getSearchTimeLine("#NowPlaying", paging);

        printTimeline("Now", comments);
    }

    @Test
    public void testUserMediaTimelineMastodon() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getMastodonAccount();
        Pageable<Comment> comments = account.action().getSearchTimeLine("#NowPlaying", paging);

        printTimeline("Now", comments);
    }
}
