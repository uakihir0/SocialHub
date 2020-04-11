package net.socialhub.apis.addition;

import net.socialhub.SocialAuthUtil;
import net.socialhub.apis.AbstractTimelineTest;
import net.socialhub.model.Account;
import net.socialhub.model.service.Notification;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Trend;
import net.socialhub.model.service.addition.misskey.MisskeyNotification;
import net.socialhub.service.misskey.MisskeyAction;
import org.junit.Test;

public class MisskeyActionTest extends AbstractTimelineTest {

    @Test
    public void getTrendsTest() {
        Account account = SocialAuthUtil.getMisskeyAccount();
        MisskeyAction action = (MisskeyAction) account.action();

        for (Trend trend : action.getTrends(10)) {
            System.out.println("--------------------------");
            System.out.println(trend.getName());
            System.out.println(trend.getVolume());
        }
    }

    @Test
    public void getNotifications() {
        Account account = SocialAuthUtil.getMisskeyAccount();
        MisskeyAction action = (MisskeyAction) account.action();
        Pageable<Notification> models = action.getNotification(new Paging(100L));

        for (Notification notification : models.getEntities()) {
            System.out.println("--------------------------");
            System.out.println("Type: " + notification.getType());

            if (notification instanceof MisskeyNotification) {
                System.out.println("Reaction: " + ((MisskeyNotification) notification).getReaction());
                System.out.println("Icon Url " + ((MisskeyNotification) notification).getIconUrl());
            }
        }
    }
}
