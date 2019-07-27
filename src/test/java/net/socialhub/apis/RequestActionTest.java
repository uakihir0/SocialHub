package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.define.action.TimeLineActionType;
import net.socialhub.model.Account;
import net.socialhub.service.action.RequestAction;
import org.junit.Test;

import java.util.ArrayList;

public class RequestActionTest extends AbstractTimelineTest {
    
    @Test
    public void testRequestActionMastodon() {

        Account account = SocialAuthUtil.getMastodonAccount();
        RequestAction action = RequestAction.of(account.action(), //
                TimeLineActionType.HomeTimeLine, new ArrayList<>());

        printTimeline("TEST", action.getComments(null));
    }
}
