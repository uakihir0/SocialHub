package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Pageable;
import net.socialhub.core.model.service.Paging;
import net.socialhub.core.model.service.Thread;
import org.junit.Test;

public class MessageTimelineTest extends AbstractTimelineTest {

    @Test
    public void testMessageTimelineMastodon() {

        Account account = SocialAuthUtil.getMastodonAccount();
        Pageable<Thread> threads = account.action().getMessageThread(new Paging(50L));

        if (threads.getEntities().size() > 0) {
            Pageable<Comment> comments = account.action().getMessageTimeLine(
                    threads.getEntities().get(0), new Paging(50L));
            printTimeline("Message", comments);
        }
    }

    @Test
    public void testMessageTimelineMisskey() {

        Account account = SocialAuthUtil.getMisskeyAccount();
        Pageable<Thread> threads = account.action().getMessageThread(new Paging(50L));

        if (threads.getEntities().size() > 0) {
            Pageable<Comment> comments = account.action().getMessageTimeLine(
                    threads.getEntities().get(0), new Paging(50L));
            printTimeline("Message", comments);
        }
    }
}

