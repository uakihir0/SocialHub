package net.socialhub.apis.addition;

import net.socialhub.SocialAuthUtil;
import net.socialhub.apis.AbstractTimelineTest;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Notification;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Trend;
import net.socialhub.model.service.User;
import net.socialhub.service.mastodon.MastodonAction;
import org.junit.Test;

import java.util.List;

public class MastodonActionTest extends AbstractTimelineTest {

    @Test
    public void getTrendsTest() {
        Account account = SocialAuthUtil.getMastodonAccount();
        MastodonAction action = (MastodonAction) account.action();

        for (Trend trend : action.getTrends(10)) {
            System.out.println("--------------------------");
            System.out.println(trend.getName());
            System.out.println(trend.getVolume());
        }
    }

    @Test
    public void getNotifications() {
        Account account = SocialAuthUtil.getMastodonAccount();
        MastodonAction action = (MastodonAction) account.action();
        Pageable<Notification> models = action.getNotification(new Paging(100L));

        for (Notification notification : models.getEntities()) {
            System.out.println("--------------------------");
            System.out.println(notification.getType());
            System.out.println(notification.getCreateAt());
        }
    }

    @Test
    public void getUserPinedComments() {
        Account account = SocialAuthUtil.getMastodonAccount();
        MastodonAction action = (MastodonAction) account.action();

        User me = action.getUserMe();
        List<Comment> comments = action.getUserPinedComments(me);
        for (Comment comment : comments) {
            printComment(comment);
        }
    }
}
