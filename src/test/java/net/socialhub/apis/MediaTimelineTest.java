package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.User;
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
