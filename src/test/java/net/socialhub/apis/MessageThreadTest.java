package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Thread;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.misskey.MisskeyThread;
import org.junit.Test;

public class MessageThreadTest extends AbstractApiTest {

    @Test
    public void testMessageThreadTest_Twitter() {

        Account account = SocialAuthUtil.getTwitterAccount();
        Pageable<Thread> threads = account.action().getMessageThread(new Paging(50L));
        threads.getEntities().forEach(this::printThread);
    }

    @Test
    public void testMessageThreadTest_Slack() {

        Account account = SocialAuthUtil.getSlackAccount();
        Pageable<Thread> threads = account.action().getMessageThread(new Paging(50L));
        threads.getEntities().forEach(this::printThread);
    }

    @Test
    public void testMessageThreadTest_Mastodon() {

        Account account = SocialAuthUtil.getMastodonAccount();
        Pageable<Thread> threads = account.action().getMessageThread(new Paging(50L));
        threads.getEntities().forEach(this::printThread);
    }

    @Test
    public void testMessageThreadTest_Misskey() {

        Account account = SocialAuthUtil.getMisskeyAccount();
        Pageable<Thread> threads = account.action().getMessageThread(new Paging(50L));
        threads.getEntities().forEach(this::printThread);
    }

    private void printThread(Thread thread) {

        System.out.println("===================================");
        System.out.println("ID> " + thread.getId());

        if (thread instanceof MisskeyThread) {
            System.out.println("IsGroup> " + ((MisskeyThread) thread).isGroup());
        }
        for (User user : thread.getUsers()) {
            System.out.println("ScreenName> " + user.getScreenName());
        }
    }
}
