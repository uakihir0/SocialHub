package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.slack.SlackComment;
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
            System.out.println(c.getId());

            Comment dc = c.getDisplayComment();
            System.out.println("T> " + dc.getText());

            if (c instanceof SlackComment) {
                System.out.println("C> " + ((SlackComment) c).getChannel());
            }

            if (dc.getApplication() != null) {
                System.out.println("A> " + dc.getApplication().getName());
            }

            for (Media m : dc.getMedias()) {
                System.out.println("M> " + m.getType() + " : " + m.getPreviewUrl());
            }

            for (Reaction m : dc.getReactions()) {
                System.out.println("R> " + m.getEmoji() + " : " + m.getCount());
            }
        }
    }
}
