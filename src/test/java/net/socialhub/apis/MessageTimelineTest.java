package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.Thread;
import org.junit.Test;

public class MessageTimelineTest extends AbstractTimelineTest {

    @Test
    public void testMessageTimelineMastodon() {

        Account account = getMastodonAccount();
        Pageable<Thread> threads = account.action().getMessageThread(new Paging(50L));

        if (threads.getEntities().size() > 0) {
            Pageable<Comment> comments = account.action().getMessageTimeLine(
                    threads.getEntities().get(0), new Paging(50L));
            printTimeline("Message", comments);
        }
    }

    @Test
    public void testMessageTimelineMisskey() {

        Account account = getMisskeyAccount();
        Pageable<Thread> threads = account.action().getMessageThread(new Paging(50L));

        if (threads.getEntities().size() > 0) {
            Pageable<Comment> comments = account.action().getMessageTimeLine(
                    threads.getEntities().get(0), new Paging(50L));
            printTimeline("Message", comments);
        }
    }
}

