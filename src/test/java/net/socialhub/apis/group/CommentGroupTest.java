package net.socialhub.apis.group;

import net.socialhub.SocialAuthUtil;
import net.socialhub.apis.AbstractApiTest;
import net.socialhub.model.group.AccountGroup;
import net.socialhub.model.group.CommentGroup;
import net.socialhub.model.service.Comment;
import org.junit.Test;

public class CommentGroupTest extends AbstractApiTest {

    @Test
    public void testHomeTimeline() {

        AccountGroup accounts = new AccountGroup();
        accounts.addAccount(SocialAuthUtil.getSlackAccount());
        accounts.addAccount(SocialAuthUtil.getMastodonAccount());
        accounts.addAccount(SocialAuthUtil.getTwitterAccount());

        CommentGroup comments = accounts.action().getHomeTimeLine();
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
