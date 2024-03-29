package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.User;
import org.junit.Test;

public class SearchUserTest extends AbstractTimelineTest {

    @Test
    public void testSearchUser_Misskey() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = getMisskeyAccount();
        Pageable<User> users = account.action().searchUsers("a", paging);
        for (User user : users.getEntities()) {
            System.out.println(user.getName());
        }

        paging = users.pastPage();
        Pageable<User> nexts = account.action().searchUsers("a", paging);
        for (User next : nexts.getEntities()) {
            System.out.println(next.getName());
        }
    }

    @Test
    public void testSearchUser_Bluesky() {
        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = getBlueskyAccount();
        Pageable<User> users = account.action().searchUsers("うるし", paging);

        for (User user : users.getEntities()) {
            System.out.println(user.getName());
        }
    }
}
