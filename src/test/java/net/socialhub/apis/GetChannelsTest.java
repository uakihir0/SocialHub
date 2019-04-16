package net.socialhub.apis;

import com.github.seratch.jslack.api.methods.request.channels.ChannelsListRequest;
import net.socialhub.SocialHub;
import net.socialhub.TestProperty;
import net.socialhub.model.Account;
import net.socialhub.service.slack.SlackAuth;
import org.junit.Test;

public class GetChannelsTest extends AbstractApiTest {

    @Test
    public void testSlackListAll() throws Exception {

        SlackAuth auth = SocialHub.getSlackAuth( //
                TestProperty.SlackProperty.ClientId, //
                TestProperty.SlackProperty.ClientSecret);

        Account account = auth.getAccountWithToken( //
                TestProperty.SlackProperty.Token);

        auth.getAccessor().getSlack().methods().channelsList(
                ChannelsListRequest.builder().token(auth.getAccessor().getToken()).build());

    }
}
