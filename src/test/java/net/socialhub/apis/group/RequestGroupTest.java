package net.socialhub.apis.group;

import net.socialhub.apis.AbstractApiTest;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.group.CommentGroup;
import net.socialhub.core.model.group.CommentsRequestGroup;
import org.junit.Test;

public class RequestGroupTest extends AbstractApiTest {

    @Test
    public void testHomeTimeline() {

        CommentsRequestGroup request = CommentsRequestGroup.of();
        request.addCommentsRequests(getSlackAccount().request().getHomeTimeLine());
        request.addCommentsRequests(getMastodonAccount().request().getHomeTimeLine());
        request.addCommentsRequests(getTwitterAccount().request().getHomeTimeLine());

        CommentGroup comments = request.action().getComments();
        CommentGroup pasts = comments.action().getPastComments();

        System.out.println("========================");
        System.out.println("> Now");
        System.out.println("========================");

        for (Comment c : comments.getComments().getEntities()) {
            System.out.println(c.getCreateAt().getTime() + ":" + c.getService().getType() + ": " + c.getText());
        }

        System.out.println("========================");
        System.out.println("> Past");
        System.out.println("========================");

        for (Comment c : pasts.getComments().getEntities()) {
            System.out.println(c.getCreateAt().getTime() + ":" + c.getService().getType() + ": " + c.getText());
        }
    }
}