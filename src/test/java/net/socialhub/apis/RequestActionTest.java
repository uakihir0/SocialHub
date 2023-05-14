package net.socialhub.apis;

import net.socialhub.core.action.request.CommentsRequest;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Paging;
import org.junit.Test;

public class RequestActionTest extends AbstractTimelineTest {

    @Test
    public void testRequestActionMastodon() {

        Account account = getMastodonAccount();
        CommentsRequest request = account.request().getHomeTimeLine();

        printTimeline("TEST", request.getComments(new Paging(10L)));
    }
}
