package net.socialhub.apis.addition;

import net.socialhub.apis.AbstractTimelineTest;
import net.socialhub.core.action.request.UsersRequest;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Channel;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.Trend;
import net.socialhub.core.model.User;
import net.socialhub.core.model.support.TrendComment;
import net.socialhub.core.model.support.TrendCountry;
import net.socialhub.core.model.support.TrendCountry.TrendLocation;
import net.socialhub.service.twitter.action.TwitterAction;
import net.socialhub.service.twitter.action.TwitterRequest;
import org.junit.Test;

import java.util.List;

public class TwitterActionTest extends AbstractTimelineTest {

    @Test
    public void getTrendsTest() {
        Account account = getTwitterAccount();
        TwitterAction action = (TwitterAction) account.action();

        for (Trend trend : action.getTrends(1)) {
            System.out.println(trend.getName());
        }
    }

    @Test
    public void getTrendLocations() {
        Account account = getTwitterAccount();
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
        Account account = getTwitterAccount();
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
        Account account = getTwitterAccount();
        TwitterAction action = (TwitterAction) account.action();

        for (String search : action.getSavedSearch()) {
            System.out.println(search);
        }
    }

    @Test
    public void getFollowingList() {
        Account account = getTwitterAccount();
        TwitterAction action = (TwitterAction) account.action();
        Pageable<Channel> channels = action.getUserFollowingChannel(action.getUserMe(), new Paging(200L));

        for (Channel channel : channels.getEntities()) {
            System.out.println(channel.getName());
        }
    }

    @Test
    public void getListedList() {
        Account account = getTwitterAccount();
        TwitterAction action = (TwitterAction) account.action();
        Pageable<Channel> channels = action.getUserListedChannel(action.getUserMe(), new Paging(200L));

        for (Channel channel : channels.getEntities()) {
            System.out.println(channel.getName());
        }
    }

    @Test
    public void getReactionUsers() {
        Account account = getTwitterAccount();
        TwitterAction action = (TwitterAction) account.action();

        Identify id = new Identify(account.getService(), 25073877L);
        Pageable<Comment> comment = action.getUserCommentTimeLine(id, new Paging(1L));

        Comment c = comment.getEntities().get(0);

        Pageable<User> favoriteUsers = action.getUsersFavoriteBy(c, new Paging(100L));
        System.out.println("// ================================= //");
        System.out.println("// Favorite //");
        for (User user : favoriteUsers.getEntities()) {
            System.out.println(user.getName());
        }

        Pageable<User> retweetUsers = action.getUsersRetweetBy(c, new Paging(100L));
        System.out.println("// ================================= //");
        System.out.println("// Retweet //");
        for (User user : retweetUsers.getEntities()) {
            System.out.println(user.getName());
        }
    }

    @Test
    public void getReactionUsersFromRequest() {
        Account account = getTwitterAccount();
        TwitterAction action = (TwitterAction) account.action();

        Identify id = new Identify(account.getService(), 25073877L);
        Pageable<Comment> comment = action.getUserCommentTimeLine(id, new Paging(1L));

        Comment c = comment.getEntities().get(0);

        UsersRequest favoriteUsers = ((TwitterRequest) action.request()).getUsersFavoriteBy(c);
        System.out.println("// ================================= //");
        System.out.println("// Favorite //");
        for (User user : favoriteUsers.getUsers(new Paging(100L)).getEntities()) {
            System.out.println(user.getName());
        }

        UsersRequest retweetUsers = ((TwitterRequest) action.request()).getUsersRetweetBy(c);
        System.out.println("// ================================= //");
        System.out.println("// Retweet //");
        for (User user : retweetUsers.getUsers(new Paging(100L)).getEntities()) {
            System.out.println(user.getName());
        }
    }

    @Test
    public void getUserPinedComments() {
        Account account = getTwitterAccount();
        TwitterAction action = (TwitterAction) account.action();

        User me = action.getUserMe();
        List<Comment> comments = action.getUserPinedComments(me);
        for (Comment comment : comments) {
            printComment(comment);
        }
    }
}
