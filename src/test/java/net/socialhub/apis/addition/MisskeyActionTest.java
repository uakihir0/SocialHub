package net.socialhub.apis.addition;

import net.socialhub.apis.AbstractTimelineTest;
import net.socialhub.core.action.RequestAction;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Notification;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.Trend;
import net.socialhub.core.model.group.CommentGroup;
import net.socialhub.core.model.group.CommentsRequestGroup;
import net.socialhub.logger.Logger;
import net.socialhub.service.misskey.action.MisskeyAction;
import net.socialhub.service.misskey.action.MisskeyRequest;
import net.socialhub.service.misskey.model.MisskeyNotification;
import org.junit.Test;

public class MisskeyActionTest extends AbstractTimelineTest {

    @Test
    public void getTrendsTest() {
        Account account = getMisskeyAccount();
        MisskeyAction action = (MisskeyAction) account.action();

        for (Trend trend : action.getTrends(10)) {
            System.out.println("--------------------------");
            System.out.println(trend.getName());
            System.out.println(trend.getVolume());
        }
    }

    @Test
    public void getNotifications() {
        Account account = getMisskeyAccount();
        MisskeyAction action = (MisskeyAction) account.action();
        Logger.getLogger(null).setLogLevel(Logger.LogLevel.ERROR);

        Pageable<Notification> models = action.getNotification(new Paging(10L));

        System.out.println("========================");
        System.out.println("> Now");
        System.out.println("========================");
        for (Notification notification : models.getEntities()) {
            System.out.println("--------------------------");
            System.out.println("Type: " + notification.getType());

            if (notification instanceof MisskeyNotification) {
                System.out.println("Reaction: " + ((MisskeyNotification) notification).getReaction());
                System.out.println("Icon Url " + ((MisskeyNotification) notification).getIconUrl());
            }
        }
    }

    @Test
    public void getFeaturedTimeline() {

        CommentsRequestGroup request = CommentsRequestGroup.of();
        RequestAction r = getMisskeyAccount().request();
        request.addCommentsRequests(((MisskeyRequest) r).getFeaturedTimeLine());

        CommentGroup comments = request.action().getComments();
        CommentGroup news = comments.action().getNewComments();

        System.out.println("========================");
        System.out.println("> Now");
        System.out.println("========================");

        for (Comment c : comments.getComments().getEntities()) {
            System.out.println(c.getCreateAt().getTime() + ":"
                    + c.getService().getType() + ": "
                    + c.getText().getDisplayText());
        }

        System.out.println("========================");
        System.out.println("> New");
        System.out.println("========================");

        for (Comment c : news.getComments().getEntities()) {
            System.out.println(c.getCreateAt().getTime() + ":"
                    + c.getService().getType() + ": "
                    + c.getText().getDisplayText());
        }
    }
}
