package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.User;
import org.junit.Test;

public class MediaTimelineTest extends AbstractTimelineTest {

    @Test
    public void testUserMediaTimelineTwitter_Past() {
        execPast(getTwitterAccount());
    }

    @Test
    public void testUserMediaTimelineBluesky_Past() {
        execPast(getBlueskyAccount());
    }

    private void execPast(Account account) {
        Paging paging = new Paging();
        paging.setCount(10L);

        User user = account.action().getUserMe();
        Pageable<Comment> comments = account.action().getUserMediaTimeLine(user, paging);
        Pageable<Comment> pasts = account.action().getUserMediaTimeLine(user, comments.pastPage());

        printTimeline("Now", comments);
        printTimeline("Past", pasts);
    }
}
