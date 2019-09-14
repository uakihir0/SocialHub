package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Paging;
import net.socialhub.service.action.request.CommentsRequest;
import org.junit.Test;

public class RequestActionTest extends AbstractTimelineTest {

    @Test
    public void testRequestActionMastodon() {

        Account account = SocialAuthUtil.getMastodonAccount();
        CommentsRequest request = account.request().getHomeTimeLine();

        printTimeline("TEST", request.getComments(new Paging(10L)));
    }
}
