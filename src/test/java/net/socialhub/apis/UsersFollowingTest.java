package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.User;
import org.junit.Test;

public class UsersFollowingTest extends AbstractApiTest {

    @Test
    public void testGetFollowingTwitter() {
        Account account = SocialAuthUtil.getTwitterAccount();
        User me = account.action().getUserMe();

        Pageable<User> users = account.action().getFollowingUsers(me, null);
        for (User user : users.getEntities()) {
            System.out.println(user.getName());
        }
    }

    @Test
    public void testGetFollowerTwitter() {
        Account account = SocialAuthUtil.getTwitterAccount();
        User me = account.action().getUserMe();

        Pageable<User> users = account.action().getFollowerUsers(me, null);
        for (User user : users.getEntities()) {
            System.out.println(user.getName());
        }
    }
}
