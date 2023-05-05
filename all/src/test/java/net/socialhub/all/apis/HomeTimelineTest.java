package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Pageable;
import net.socialhub.core.model.service.Paging;
import org.junit.Test;

public class HomeTimelineTest extends AbstractTimelineTest {

    @Test
    public void testHomeTimelineTwitter_New() {
        execNew(SocialAuthUtil.getTwitterAccount());
    }

    @Test
    public void testHomeTimelineTwitter_Past() {
        execPast(SocialAuthUtil.getTwitterAccount());
    }

    @Test
    public void testHomeTimelineMastodon_New() {
        execNew(SocialAuthUtil.getMastodonAccount());
    }

    @Test
    public void testHomeTimelineMastodon_Past() {
        execPast(SocialAuthUtil.getMastodonAccount());
    }

    @Test
    public void testHomeTimelinePixelFed_New() {
        execNew(SocialAuthUtil.getPixelFedAccount());
    }

    @Test
    public void testHomeTimelinePixelFed_Past() {
        execPast(SocialAuthUtil.getPixelFedAccount());
    }

    @Test
    public void testHomeTimelinePleroma_New() {
        execNew(SocialAuthUtil.getPleromaAccount());
    }

    @Test
    public void testHomeTimelinePleroma_Past() {
        execPast(SocialAuthUtil.getPleromaAccount());
    }

    @Test
    public void testHomeTimelineMisskey_New() {
        execNew(SocialAuthUtil.getMisskeyAccount());
    }

    @Test
    public void testHomeTimelineMisskey_Past() {
        execPast(SocialAuthUtil.getMisskeyAccount());
    }

    @Test
    public void testHomeTimelineSlack_New() {
        execNew(SocialAuthUtil.getSlackAccount());
    }

    @Test
    public void testHomeTimelineSlack_Past() {
        execPast(SocialAuthUtil.getSlackAccount());
    }

    @Test
    public void testHomeTimelineTumblr_New() {
        execNew(SocialAuthUtil.getTumblrAccount());
    }

    @Test
    public void testHomeTimelineTumblr_Past() {
        execPast(SocialAuthUtil.getTumblrAccount());
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
