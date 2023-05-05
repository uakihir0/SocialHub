package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Pageable;
import net.socialhub.core.model.service.User;
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
