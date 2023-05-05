package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Channel;
import net.socialhub.core.model.service.Pageable;
import org.junit.Test;

public class GetChannelsTest extends AbstractApiTest {

    @Test
    public void testSlackListAll() {
        Account account = SocialAuthUtil.getSlackAccount();
        Pageable<Channel> channels = account.action().getChannels(null, null);

        for (Channel channel : channels.getEntities()) {
            System.out.println(channel.getId() + ":" + channel.getName());
        }
    }
}
