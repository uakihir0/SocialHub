package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.User;
import org.junit.Test;

import java.util.List;

public class GetFollowingsTest extends AbstractApiTest {

    @Test
    public void testGetFollowingUserNext_Twitter() {
        execNext(getTwitterAccount());
    }

    @Test
    public void testGetFollowingUserNext_Mastodon() {
        execNext(getMastodonAccount());
    }

    @Test
    public void testGetFollowingUserNext_Bluesky() {
        execNext(getBlueskyAccount());
    }

    @Test
    public void testGetFollowingUserPrev_Bluesky() {
        execPrev(getBlueskyAccount());
    }

    private void execPrev(Account account) {
        User me = account.action().getUserMe();

        Pageable<User> top = account.action().getFollowingUsers(me, new Paging(10L));
        Pageable<User> prev = account.action().getFollowingUsers(me, top.prevPage());

        System.out.println("[TOP]");
        printUsers(top.getEntities());
        System.out.println("[PREV]");
        printUsers(prev.getEntities());
    }

    private void execNext(Account account) {
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