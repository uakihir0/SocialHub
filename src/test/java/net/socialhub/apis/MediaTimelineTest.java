package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.User;
import org.junit.Test;

public class MediaTimelineTest extends AbstractTimelineTest {

    @Test
    public void testUserMediaTimelineTwitter_Past() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getTwitterAccount();
        User user = account.action().getUserMe();

        Pageable<Comment> comments = account.action().getUserMediaTimeLine(user, paging);
        Pageable<Comment> pasts = account.action().getUserMediaTimeLine(user, comments.pastPage());

        printTimeline("Now", comments);
        printTimeline("Past", pasts);
    }
}
