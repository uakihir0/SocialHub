package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import org.junit.Test;

public class HomeTimelineTest extends AbstractTimelineTest {

    @Test
    public void testHomeTimelineNew_Twitter() {
        execNew(getTwitterAccount());
    }

    @Test
    public void testHomeTimelinePast_Twitter() {
        execPast(getTwitterAccount());
    }

    @Test
    public void testHomeTimelineNew_Mastodon() {
        execNew(getMastodonAccount());
    }

    @Test
    public void testHomeTimelinePast_Mastodon() {
        execPast(getMastodonAccount());
    }

    @Test
    public void testHomeTimelineNew_PixelFed() {
        execNew(getPixelFedAccount());
    }

    @Test
    public void testHomeTimelinePast_PixelFed() {
        execPast(getPixelFedAccount());
    }

    @Test
    public void testHomeTimelineNew_Pleroma() {
        execNew(getPleromaAccount());
    }

    @Test
    public void testHomeTimelinePast_Pleroma() {
        execPast(getPleromaAccount());
    }

    @Test
    public void testHomeTimelineNew_Misskey() {
        execNew(getMisskeyAccount());
    }

    @Test
    public void testHomeTimelinePast_Misskey() {
        execPast(getMisskeyAccount());
    }

    @Test
    public void testHomeTimelineNew_Slack() {
        execNew(getSlackAccount());
    }

    @Test
    public void testHomeTimelinePast_Slack() {
        execPast(getSlackAccount());
    }

    @Test
    public void testHomeTimelineNew_Tumblr() {
        execNew(getTumblrAccount());
    }

    @Test
    public void testHomeTimelinePast_Tumblr() {
        execPast(getTumblrAccount());
    }

    @Test
    public void testHomeTimelineNew_Bluesky(){
        execNew(getBlueskyAccount());
    }

    @Test
    public void testHomeTimelinePast_Bluesky(){
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
