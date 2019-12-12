package net.socialhub.apis.addition;

import net.socialhub.SocialAuthUtil;
import net.socialhub.apis.AbstractTimelineTest;
import net.socialhub.model.Account;
import net.socialhub.model.service.Trend;
import net.socialhub.model.service.support.TrendComment;
import net.socialhub.model.service.support.TrendCountry;
import net.socialhub.model.service.support.TrendCountry.TrendLocation;
import net.socialhub.service.twitter.TwitterAction;
import org.junit.Test;

public class TwitterActionTest extends AbstractTimelineTest {

    @Test
    public void getTrendsTest() {
        Account account = SocialAuthUtil.getTwitterAccount();
        TwitterAction action = (TwitterAction) account.action();

        for (Trend trend : action.getTrends(1)) {
            System.out.println(trend.getName());
        }
    }

    @Test
    public void getTrendLocations() {
        Account account = SocialAuthUtil.getTwitterAccount();
        TwitterAction action = (TwitterAction) account.action();

        for (TrendCountry trend : action.getTrendLocations()) {
            System.out.println("==============================");
            System.out.println(trend.getId() + " : " + trend.getName());

            for (TrendLocation location : trend.getLocations()) {
                System.out.println("> " + location.getId() + " : " + location.getName());
            }
        }
    }

    @Test
    public void getTrendsComment() {
        Account account = SocialAuthUtil.getTwitterAccount();
        TwitterAction action = (TwitterAction) account.action();

        // 23424856: Japan
        for (TrendComment trend : action.getTrendsComment(23424856)) {
            System.out.println("==============================");
            System.out.println(">> " + trend.getTrend().getName());
            if (trend.getComment() != null) {
                System.out.println(trend.getComment().getDisplayComment().getText().getDisplayText());
            }
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
