package net.socialhub.service.twitter;

import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.action.SuperAccountAction;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import static net.socialhub.define.ActionEnum.*;

/**
 * Twitter Actions
 * (All Actions)
 */
public class TwitterAction extends SuperAccountAction {

    private ServiceAuth<Twitter> auth;

    // ============================================================== //
    // Account
    // ============================================================== //

    @Override
    public User getUserMe() {
        return proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User user = auth.getToken().verifyCredentials();
            service.getRateLimit().addInfo(GetUserMe, user);

            return TwitterMapper.user(user, service);
        });
    }

    @Override
    public User getUser(Identify id) {
        return proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User user = auth.getToken().showUser(id.getNumberId());

            service.getRateLimit().addInfo(GetUserMe, user);
            return TwitterMapper.user(user, service);
        });
    }

    @Override
    public void followUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getToken().createFriendship(id.getNumberId());
            service.getRateLimit().addInfo(FollowUser, after);
        });
    }

    @Override
    public void unfollowUser(Identify id) {
        proceed(() -> {
            Service service = getAccount().getService();
            twitter4j.User after = auth.getToken().destroyFriendship(id.getNumberId());
            service.getRateLimit().addInfo(UnfollowUser, after);
        });
    }

    // ============================================================== //
    // Comment
    // ============================================================== //

    @Override
    public Pageable<Comment> getHomeTimeLine(Paging paging) {
        return proceed(() -> {
            Twitter twitter = auth.getToken();
            Service service = getAccount().getService();
            ResponseList<Status> statues = (paging == null) ? twitter.getHomeTimeline() //
                    : twitter.getHomeTimeline(TwitterMapper.fromPaging(paging));

            service.getRateLimit().addInfo(HomeTimeLine, statues);
            return TwitterMapper.timeLine(statues, service, paging);
        });
    }

    @Override
    public void like(Identify id){
        proceed(() -> {
            Twitter twitter = auth.getToken();
            Status status = twitter.favorites().createFavorite(id.getNumberId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(LikeComment, status);
        });
    }

    @Override
    public void unlike(Identify id){
        proceed(() -> {
            Twitter twitter = auth.getToken();
            Status status = twitter.favorites().destroyFavorite(id.getNumberId());

            Service service = getAccount().getService();
            service.getRateLimit().addInfo(UnlikeComment, status);
        });
    }

    // ============================================================== //
    // Utils
    // ============================================================== //

    private <T> T proceed(ActionCaller<T, TwitterException> runner) {
        try {
            return runner.proceed();
        } catch (TwitterException e) {
            handleTwitterException(e);
            return null;
        }
    }

    private void proceed(ActionRunner<TwitterException> runner) {
        try {
            runner.proceed();
        } catch (TwitterException e) {
            handleTwitterException(e);
        }
    }

    private static void handleTwitterException(TwitterException e) {
        System.out.println(e.getMessage());
    }

    //region // Getter&Setter
    TwitterAction(Account account, ServiceAuth<Twitter> auth) {
        this.account(account);
        this.auth(auth);
    }

    TwitterAction auth(ServiceAuth<Twitter> auth) {
        this.auth = auth;
        return this;
    }
    //endregion
}
