package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Paging;
import net.socialhub.core.action.request.CommentsRequest;
import org.junit.Test;

public class RequestActionTest extends AbstractTimelineTest {

    @Test
    public void testRequestActionMastodon() {

        Account account = SocialAuthUtil.getMastodonAccount();
        CommentsRequest request = account.request().getHomeTimeLine();

        printTimeline("TEST", request.getComments(new Paging(10L)));
    }
}
