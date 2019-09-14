package net.socialhub.apis.group;

import net.socialhub.SocialAuthUtil;
import net.socialhub.apis.AbstractApiTest;
import net.socialhub.model.group.CommentGroup;
import net.socialhub.model.group.CommentsRequestGroup;
import net.socialhub.model.service.Comment;
import org.junit.Test;

public class RequestGroupTest extends AbstractApiTest {

    @Test
    public void testHomeTimeline() {

        CommentsRequestGroup request = CommentsRequestGroup.of();
        request.addCommentsRequests(SocialAuthUtil.getSlackAccount().request().getHomeTimeLine());
        request.addCommentsRequests(SocialAuthUtil.getMastodonAccount().request().getHomeTimeLine());
        request.addCommentsRequests(SocialAuthUtil.getTwitterAccount().request().getHomeTimeLine());

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