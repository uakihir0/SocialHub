package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Pageable;
import net.socialhub.core.model.service.Paging;
import net.socialhub.core.model.service.User;
import org.junit.Test;

import java.util.List;

public class GetFollowingsTest extends AbstractApiTest {

    @Test
    public void testGetFollowingUser_Twitter() {

        Account account = SocialAuthUtil.getTwitterAccount();
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

        Account account = SocialAuthUtil.getMastodonAccount();
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