package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Request;
import net.socialhub.service.action.request.CommentsRequest;
import net.socialhub.service.mastodon.MastodonRequest;
import org.junit.Test;

public class RequestRestoreTest extends AbstractTimelineTest {

    @Test
    public void testRestoredHomeTimelineTwitter() {
        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getTwitterAccount();
        String string = account.request().getHomeTimeLine().toSerializedString();

        System.out.println(string);

        Request request = account.request().fromSerializedString(string);

        if (request instanceof CommentsRequest) {
            Pageable<Comment> comments = ((CommentsRequest) request).getComments(paging);
            printTimeline("Comments", comments);
        }
    }

    @Test
    public void testRestoredLocalTimelineMastodon() {
        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getMastodonAccount();
        String string = ((MastodonRequest) account.request()).getLocalTimeLine().toSerializedString();

        System.out.println(string);

        Request request = account.request().fromSerializedString(string);

        if (request instanceof CommentsRequest) {
            Pageable<Comment> comments = ((CommentsRequest) request).getComments(paging);
            printTimeline("Comments", comments);
        }
    }

    @Test
    public void testRestoredHomeTimelineSlack() {
        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getSlackAccount();
        String string = account.request().getHomeTimeLine().toSerializedString();

        System.out.println(string);

        Request request = account.request().fromSerializedString(string);

        if (request instanceof CommentsRequest) {
            Pageable<Comment> comments = ((CommentsRequest) request).getComments(paging);
            printTimeline("Comments", comments);
        }
    }
}
