package net.socialhub.apis.addition;

import net.socialhub.SocialAuthUtil;
import net.socialhub.apis.AbstractTimelineTest;
import net.socialhub.model.Account;
import net.socialhub.model.service.Trend;
import net.socialhub.service.mastodon.MastodonAction;
import org.junit.Test;

public class MastodonActionTest extends AbstractTimelineTest {

    @Test
    public void getTrendsTest() {
        Account account = SocialAuthUtil.getMastodonAccount();
        MastodonAction action = (MastodonAction) account.action();

        for (Trend trend : action.getTrends(10)) {
            System.out.println("--------------------------");
            System.out.println(trend.getName());
            System.out.println(trend.getVolume());
        }
    }
}
