package net.socialhub.all.apis.addition;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.all.apis.AbstractTimelineTest;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Notification;
import net.socialhub.core.model.service.Pageable;
import net.socialhub.core.model.service.Paging;
import net.socialhub.core.model.service.Trend;
import net.socialhub.core.model.service.User;
import net.socialhub.mastodon.action.MastodonAction;
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

        Pageable<Notification> now = action.getNotification(new Paging(10L));
        Pageable<Notification> next = action.getNotification(now.nextPage());

        System.out.println("[NOW]");
        printNotification(now.getEntities());
        System.out.println("[NEXT]");
        printNotification(next.getEntities());
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

    private void printNotification(List<Notification> notifications) {
        for (Notification notification : notifications) {
            System.out.println("--------------------------");
            System.out.println(notification.getType());
            System.out.println(notification.getCreateAt());
        }
    }
}
