package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import org.junit.Test;

public class SearchTimelineTest extends AbstractTimelineTest {

    @Test
    public void testUserMediaTimeline_Twitter() {
        execPast(getTwitterAccount());
    }

    @Test
    public void testUserMediaTimeline_Mastodon() {
        execPast(getMastodonAccount());

    }

    @Test
    public void testUserMediaTimeline_Bluesky() {
        execPast(getBlueskyAccount());
    }

    private void execPast(Account account){
        Paging paging = new Paging();
        paging.setCount(10L);

        Pageable<Comment> comments = account.action().getSearchTimeLine("#NowPlaying", paging);
        Pageable<Comment> pasts = account.action().getSearchTimeLine("#NowPlaying", comments.pastPage());

        printTimeline("Now", comments);
        printTimeline("Past", pasts);
    }
}
