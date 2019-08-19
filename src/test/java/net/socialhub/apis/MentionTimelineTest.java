package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import org.junit.Test;

public class MentionTimelineTest extends AbstractTimelineTest {

    @Test
    public void testHomeTimelineMastodon() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getMastodonAccount();

        Pageable<Comment> comments = account.action().getMentionTimeLine(paging);
        printTimeline("Now", comments);
    }
}

