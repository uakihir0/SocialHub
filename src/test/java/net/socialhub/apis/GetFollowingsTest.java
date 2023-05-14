package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.User;
import org.junit.Test;

import java.util.List;

public class GetFollowingsTest extends AbstractApiTest {

    @Test
    public void testGetFollowingUser_Twitter() {

        Account account = getTwitterAccount();
        User me = account.action().getUserMe();

        Pageable<User> top = account.action().getFollowingUsers(me, new Paging(10L));
        Pageable<User> next = account.action().getFollowingUsers(me, top.nextPage());

        System.out.println("[TOP]");
        printUsers(top.getEntities());
        System.out.println("[NEXT]");
        printUsers(next.getEntities());
    }

    @Test
    public void testGetFollowingUser_Mastodon() {

        Account account = getMastodonAccount();
        User me = account.action().getUserMe();

        Pageable<User> top = account.action().getFollowingUsers(me, new Paging(10L));
        Pageable<User> next = account.action().getFollowingUsers(me, top.nextPage());

        System.out.println("[TOP]");
        printUsers(top.getEntities());
        System.out.println("[NEXT]");
        printUsers(next.getEntities());
    }

    private void printUsers(List<User> users) {
        for (User user : users) {
            System.out.println(user.getName());
            System.out.println(user.getScreenName());
        }
    }
}