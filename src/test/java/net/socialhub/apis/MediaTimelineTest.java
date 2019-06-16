package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.slack.SlackComment;
import net.socialhub.model.service.paging.BorderPaging;
import org.junit.Test;

public class MediaTimelineTest extends AbstractApiTest {

    @Test
    public void testUserMediaTimelineTwitter_Past() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getTwitterAccount();
        User user = account.action().getUserMe();

        Pageable<Comment> comments = account.action().getUserMediaTimeLine(user, paging);
        Pageable<Comment> pasts = account.action().getUserMediaTimeLine(user, comments.pastPage());

        printTimeline("Now", comments);
        printTimeline("Past", pasts);
    }


    private void printTimeline(String title, Pageable<Comment> timeline) {

        System.out.println("========================");
        System.out.println("> " + title);
        printPaging(timeline.getPaging());
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

    private void printPaging(Paging paging) {

        if (paging instanceof BorderPaging) {
            BorderPaging bp = (BorderPaging) paging;
            System.out.println("> Max: " + bp.getMaxId());
            System.out.println("> Sin: " + bp.getSinceId());
        }
    }
}
