package net.socialhub.apis;

import net.socialhub.model.service.*;
import net.socialhub.model.service.addition.slack.SlackComment;
import net.socialhub.model.service.paging.BorderPaging;

public class AbstractTimelineTest extends AbstractApiTest {

    protected void printTimeline(String title, Pageable<Comment> timeline) {

        System.out.println("========================");
        System.out.println("> " + title);
        printPaging(timeline.getPaging());
        System.out.println("========================");

        for (Comment c : timeline.getEntities()) {
            System.out.println(c.getId());
            System.out.println(c.getCreateAt());

            Comment dc = c.getDisplayComment();
            System.out.println("T> " + dc.getText().getDisplayText());

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

    protected void printPaging(Paging paging) {

        if (paging instanceof BorderPaging) {
            BorderPaging bp = (BorderPaging) paging;
            System.out.println("> Max: " + bp.getMaxId());
            System.out.println("> Sin: " + bp.getSinceId());
        }
    }
}
