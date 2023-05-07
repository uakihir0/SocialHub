package net.socialhub.apis;

import net.socialhub.core.model.common.AttributedKind;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Media;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.Poll;
import net.socialhub.core.model.Reaction;
import net.socialhub.service.microblog.model.MiniBlogComment;
import net.socialhub.service.slack.model.SlackComment;
import net.socialhub.core.model.paging.BorderPaging;
import net.socialhub.core.model.support.PollOption;

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

        dc.getText().getElements().forEach(e -> {
            if (e.getKind() == AttributedKind.EMOJI) {
                System.out.println("Emoji URL > " + e.getExpandedText());
            }
        });

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
                System.out.println("Reaction > " + m.getName() + " : " + m.getCount() + " : " + m.getIconUrl());
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
