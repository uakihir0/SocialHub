package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Media;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import org.junit.Test;

public class HomeTimelineTest extends AbstractApiTest {

    @Test
    public void testHomeTimelineTwitter() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getTwitterAccount();

        Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
        Pageable<Comment> pasts = account.action().getHomeTimeLine(comments.pastPage());

        printTimeline("Now", comments);
        printTimeline("Pasts", pasts);
    }

    @Test
    public void testHomeTimelineMastodon() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getMastodonAccount();

        Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
        Pageable<Comment> pasts = account.action().getHomeTimeLine(comments.pastPage());

        printTimeline("Now", comments);
        printTimeline("Pasts", pasts);
    }

    @Test
    public void testHomeTimelineSlack() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getSlackAccount();

        Pageable<Comment> comments = account.action().getHomeTimeLine(paging);
        Pageable<Comment> pasts = account.action().getHomeTimeLine(comments.pastPage());

        printTimeline("Now", comments);
        printTimeline("Pasts", pasts);
    }

    private void printTimeline(String title, Pageable<Comment> timeline) {

        System.out.println("========================");
        System.out.println("> " + title);
        System.out.println("========================");

        for (Comment c : timeline.getEntities()) {
            System.out.println(c.getId() + ": " + c.getText());

            if (c.getSharedComment() != null) {
                System.out.println("S> " + c.getSharedComment().getText());
            }

            if (c.getApplication() != null) {
                System.out.println("A> " + c.getApplication().getName() + " " + c.getApplication().getWebsite());
            }

            for (Media m : c.getMedias()) {
                System.out.println("M> " + m.getType() + " : " + m.getPreviewUrl());
            }
        }
    }
}
