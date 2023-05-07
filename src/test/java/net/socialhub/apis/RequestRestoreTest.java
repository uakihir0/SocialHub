package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.Request;
import net.socialhub.core.action.request.CommentsRequest;
import net.socialhub.service.mastodon.action.MastodonRequest;
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
