package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.service.twitter.TwitterAction;
import org.junit.Test;

public class TwitterActionTest extends AbstractTimelineTest {

    @Test
    public void getTrendsTest() {
        Account account = SocialAuthUtil.getTwitterAccount();
        TwitterAction action = (TwitterAction) account.action();

        for (String trend : action.getTrends(1)) {
            System.out.println(trend);
        }
    }

    @Test
    public void getSavedSearch() {
        Account account = SocialAuthUtil.getTwitterAccount();
        TwitterAction action = (TwitterAction) account.action();

        for (String search : action.getSavedSearch()) {
            System.out.println(search);
        }
    }
}
