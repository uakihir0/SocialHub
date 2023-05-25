package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.User;
import org.junit.Test;

public class UsersFollowingTest extends AbstractApiTest {

    @Test
    public void testGetFollowing_Twitter() {
        Account account = getTwitterAccount();
        User me = account.action().getUserMe();

        Pageable<User> users = account.action().getFollowingUsers(me, null);
        for (User user : users.getEntities()) {
            System.out.println(user.getName());
        }
    }

    @Test
    public void testGetFollower_Twitter() {
        Account account = getTwitterAccount();
        User me = account.action().getUserMe();

        Pageable<User> users = account.action().getFollowerUsers(me, null);
        for (User user : users.getEntities()) {
            System.out.println(user.getName());
        }
    }

    @Test
    public void testGetFollowing_Bluesky() {
        Account account = getBlueskyAccount();
        User me = account.action().getUserMe();

        Paging paging = new Paging(10L);
        Pageable<User> users = account.action().getFollowingUsers(me, paging);
        Pageable<User> pasts = account.action().getFollowingUsers(me, users.pastPage());

        for (User user : users.getEntities()) {
            System.out.println(user.getName());
        }
        for (User past : pasts.getEntities()) {
            System.out.println(past.getName());
        }
    }
}
