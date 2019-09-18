package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Channel;
import net.socialhub.model.service.Pageable;
import org.junit.Test;

public class GetChannelsTest extends AbstractApiTest {

    @Test
    public void testSlackListAll() {

        Account account = SocialAuthUtil.getSlackAccount();

        Pageable<Channel> channels = account.action().getChannels(null, null);

        for (Channel channel : channels.getEntities()) {
            System.out.println(channel.getName());
        }
    }
}
