package net.socialhub.apis;

import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Media;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Poll;
import net.socialhub.model.service.Reaction;
import net.socialhub.model.service.addition.MiniBlogComment;
import net.socialhub.model.service.addition.slack.SlackComment;
import net.socialhub.model.service.paging.BorderPaging;
import net.socialhub.model.service.support.PollOption;

public class AbstractTimelineTest extends AbstractApiTest {

    protected void printTimeline(String title, Pageable<Comment> timeline) {

        System.out.println("========================");
        System.out.println("> " + title);
        printPaging(timeline.getPaging());
        System.out.println("========================");

        for (Comment c : timeline.getEntities()) {
            printComment(c);
        }
    }

    protected void printComment(Comment c) {

        System.out.println("------------------------");
        System.out.println("ID > " + c.getId());
        System.out.println("Date > " + c.getCreateAt());

        Comment dc = c.getDisplayComment();
        System.out.println("Text > " + dc.getText().getDisplayText());
        System.out.println("Url > " + dc.getWebUrl());

        if (c instanceof SlackComment) {
            System.out.println("Channel > " + ((SlackComment) c).getChannel());
        }

        if (dc.getApplication() != null) {
            System.out.println("App > " + dc.getApplication().getName());
        }

        if (c instanceof MiniBlogComment) {
            Poll poll = ((MiniBlogComment) c).getPoll();
            if (poll != null) {
                for (PollOption option : poll.getOptions()) {
                    System.out.println("Poll Options > " + option.getTitle());
                    System.out.println("Poll Count   > " + option.getCount());
                }
            }
        }

        for (Media m : dc.getMedias()) {
            System.out.println("Media > " + m.getType());
            System.out.println("M Source > " + m.getSourceUrl());
            System.out.println("M Preview > " + m.getPreviewUrl());
        }

        for (Reaction m : dc.getReactions()) {
            if (m.getCount() > 0) {
                System.out.println("Reaction > " + m.getName() + " : " + m.getCount());
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
