package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import org.junit.Test;

public class MentionTimelineTest extends AbstractTimelineTest {

    @Test
    public void testMentionTimeline_Mastodon() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = getMastodonAccount();

        Pageable<Comment> comments = account.action().getMentionTimeLine(paging);
        printTimeline("Now", comments);
    }

    @Test
    public void testMentionTimeline_Misskey() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = getMisskeyAccount();

        Pageable<Comment> comments = account.action().getMentionTimeLine(paging);
        printTimeline("Now", comments);
    }

    @Test
    public void testMentionTimelineNew_Bluesky() {
        execNew(getBlueskyAccount());
    }

    @Test
    public void testMentionTimelinePast_Bluesky() {
        execPast(getBlueskyAccount());
    }

    private void execNew(Account account) {
        Paging paging = new Paging();
        paging.setCount(10L);

        Pageable<Comment> comments = account.action().getMentionTimeLine(paging);
        Pageable<Comment> news = account.action().getMentionTimeLine(comments.newPage());

        printTimeline("New", news);
        printTimeline("Now", comments);
    }

    private void execPast(Account account) {
        Paging paging = new Paging();
        paging.setCount(10L);

        Pageable<Comment> comments = account.action().getMentionTimeLine(paging);
        Pageable<Comment> pasts = account.action().getMentionTimeLine(comments.pastPage());

        printTimeline("Now", comments);
        printTimeline("Past", pasts);
    }
}

