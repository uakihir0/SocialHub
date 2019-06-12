package net.socialhub.apis.group;

import net.socialhub.SocialAuthUtil;
import net.socialhub.apis.AbstractApiTest;
import net.socialhub.model.group.CommentGroup;
import net.socialhub.model.group.RequestGroup;
import net.socialhub.model.service.Comment;
import org.junit.Test;

import static net.socialhub.define.action.TimeLineActionType.HomeTimeLine;

public class RequestGroupTest extends AbstractApiTest {

    @Test
    public void testHomeTimeline() {

        RequestGroup request = RequestGroup.of();
        request.addAccount(SocialAuthUtil.getSlackAccount(), HomeTimeLine);
        request.addAccount(SocialAuthUtil.getMastodonAccount(), HomeTimeLine);
        request.addAccount(SocialAuthUtil.getTwitterAccount(), HomeTimeLine);

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