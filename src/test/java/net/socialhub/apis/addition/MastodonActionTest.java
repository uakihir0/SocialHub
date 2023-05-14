package net.socialhub.apis.addition;

import net.socialhub.apis.AbstractTimelineTest;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Notification;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.Trend;
import net.socialhub.core.model.User;
import net.socialhub.service.mastodon.action.MastodonAction;
import org.junit.Test;

import java.util.List;

public class MastodonActionTest extends AbstractTimelineTest {

    @Test
    public void getTrendsTest() {
        Account account = getMastodonAccount();
        MastodonAction action = (MastodonAction) account.action();

        for (Trend trend : action.getTrends(10)) {
            System.out.println("--------------------------");
            System.out.println(trend.getName());
            System.out.println(trend.getVolume());
        }
    }

    @Test
    public void getNotifications() {
        Account account = getMastodonAccount();
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
        Account account = getMastodonAccount();
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
