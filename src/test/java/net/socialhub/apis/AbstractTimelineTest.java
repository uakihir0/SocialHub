package net.socialhub.apis;

import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Media;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Reaction;
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
            System.out.println("Text > " + dc.getText().getDisplayText());

            if (c instanceof SlackComment) {
                System.out.println("Channel > " + ((SlackComment) c).getChannel());
            }
            if (dc.getApplication() != null) {
                System.out.println("App > " + dc.getApplication().getName());
            }
            for (Media m : dc.getMedias()) {
                System.out.println("Media > " + m.getType());
                System.out.println("M Source > " + m.getSourceUrl());
                System.out.println("M Preview > " + m.getPreviewUrl());
            }
            for (Reaction m : dc.getReactions()) {
                System.out.println("Reaction > " + m.getEmoji() + " : " + m.getCount());
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
