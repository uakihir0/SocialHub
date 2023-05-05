package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Pageable;
import net.socialhub.core.model.service.Paging;
import net.socialhub.core.model.service.User;
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
