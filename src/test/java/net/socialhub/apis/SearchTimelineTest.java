package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import org.junit.Test;

public class SearchTimelineTest extends AbstractTimelineTest {

    @Test
    public void testUserMediaTimelineTwitter() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = getTwitterAccount();
        Pageable<Comment> comments = account.action().getSearchTimeLine("#NowPlaying", paging);

        printTimeline("Now", comments);
    }

    @Test
    public void testUserMediaTimelineMastodon() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = getMastodonAccount();
        Pageable<Comment> comments = account.action().getSearchTimeLine("#NowPlaying", paging);

        printTimeline("Now", comments);
    }
}
