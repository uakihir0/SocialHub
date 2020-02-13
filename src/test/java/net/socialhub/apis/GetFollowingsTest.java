package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.User;
import net.socialhub.model.service.addition.MiniBlogUser;
import org.junit.Test;

public class GetFollowingsTest extends AbstractApiTest {

    @Test
    public void testGetUserMeTwitter() {

        Account account = SocialAuthUtil.getTwitterAccount();
        Identify id = new Identify(account.getService());
        id.setId(0L);

        Pageable<User> users = account.action().getFollowingUsers(id, new Paging(200L));
    }
}