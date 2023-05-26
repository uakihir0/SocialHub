package net.socialhub.apis.addition;

import net.socialhub.apis.AbstractTimelineTest;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Notification;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.service.bluesky.action.BlueskyAction;
import net.socialhub.service.mastodon.action.MastodonAction;
import org.junit.Test;

public class BlueskyActionTest extends AbstractTimelineTest {

    @Test
    public void getNotifications() {
        Account account = getBlueskyAccount();
        BlueskyAction action = (BlueskyAction) account.action();

        Pageable<Notification> now = action.getNotification(new Paging(10L));
        Pageable<Notification> next = action.getNotification(now.nextPage());

        System.out.println("[NOW]");
        printNotification(now.getEntities());
        System.out.println("[NEXT]");
        printNotification(next.getEntities());
    }
}
