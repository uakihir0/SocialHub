package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Channel;
import net.socialhub.core.model.Pageable;
import org.junit.Test;

public class GetChannelsTest extends AbstractApiTest {

    @Test
    public void testSlackListAll() {
        Account account = getSlackAccount();
        Pageable<Channel> channels = account.action().getChannels(null, null);

        for (Channel channel : channels.getEntities()) {
            System.out.println(channel.getId() + ":" + channel.getName());
        }
    }
}
