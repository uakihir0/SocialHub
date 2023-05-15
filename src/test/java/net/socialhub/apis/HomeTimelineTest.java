package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import org.junit.Test;

public class HomeTimelineTest extends AbstractTimelineTest {

    @Test
    public void testHomeTimelineTwitter_New() {
        execNew(getTwitterAccount());
    }

    @Test
    public void testHomeTimelineTwitter_Past() {
        execPast(getTwitterAccount());
    }

    @Test
    public void testHomeTimelineMastodon_New() {
        execNew(getMastodonAccount());
    }

    @Test
    public void testHomeTimelineMastodon_Past() {
        execPast(getMastodonAccount());
    }

    @Test
    public void testHomeTimelinePixelFed_New() {
        execNew(getPixelFedAccount());
    }

    @Test
    public void testHomeTimelinePixelFed_Past() {
        execPast(getPixelFedAccount());
    }

    @Test
    public void testHomeTimelinePleroma_New() {
        execNew(getPleromaAccount());
    }

    @Test
    public void testHomeTimelinePleroma_Past() {
        execPast(getPleromaAccount());
    }

    @Test
    public void testHomeTimelineMisskey_New() {
        execNew(getMisskeyAccount());
    }

    @Test
    public void testHomeTimelineMisskey_Past() {
        execPast(getMisskeyAccount());
    }

    @Test
    public void testHomeTimelineSlack_New() {
        execNew(getSlackAccount());
    }

    @Test
    public void testHomeTimelineSlack_Past() {
        execPast(getSlackAccount());
    }

    @Test
    public void testHomeTimelineTumblr_New() {
        execNew(getTumblrAccount());
    }

    @Test
    public void testHomeTimelineTumblr_Past() {
        execPast(getTumblrAccount());
    }

    @Test
    public void testHomeTimelineBluesky_Past(){
        execPast(getBlueskyAccount());
    }

    private void execNew(Account account) {
        Paging paging = new Paging();
        paging.setCount(10L);

        Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
        Pageable<Comment> news = account.action().getHomeTimeLine(comments.newPage());

        printTimeline("New", news);
        printTimeline("Now", comments);
    }

    private void execPast(Account account) {
        Paging paging = new Paging();
        paging.setCount(10L);

        Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
        Pageable<Comment> pasts = account.action().getHomeTimeLine(comments.pastPage());

        printTimeline("Now", comments);
        printTimeline("Past", pasts);
    }
}
