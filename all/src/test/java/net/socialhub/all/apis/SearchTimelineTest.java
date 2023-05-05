package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Pageable;
import net.socialhub.core.model.service.Paging;
import org.junit.Test;

public class SearchTimelineTest extends AbstractTimelineTest {

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
