package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Thread;
import net.socialhub.model.service.User;
import org.junit.Test;

import java.util.List;

public class MessageThreadTest extends AbstractApiTest {

    @Test
    public void testMessageThreadTest_Twitter() {

        Account account = SocialAuthUtil.getTwitterAccount();
        List<Thread> threads = account.action().getMessageThread(new Paging(50L));
        threads.forEach(this::printThread);
    }

    private void printThread(Thread thread) {
        System.out.println("===================================");
        for (User user : thread.getUsers()) {
            System.out.println("U> " + user.getName());
        }
        for (Comment comment : thread.getComments()) {
            System.out.println(" CU> " + comment.getUser().getName());
            System.out.println(" CT> " + comment.getText().getDisplayText());
        }
    }
}
