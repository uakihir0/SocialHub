package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Pageable;
import net.socialhub.core.model.service.Paging;
import org.junit.Test;

public class MentionTimelineTest extends AbstractTimelineTest {

    @Test
    public void testMentionTimelineMastodon() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getMastodonAccount();

        Pageable<Comment> comments = account.action().getMentionTimeLine(paging);
        printTimeline("Now", comments);
    }

    @Test
    public void testMentionTimelineMisskey() {

        Paging paging = new Paging();
        paging.setCount(10L);

        Account account = SocialAuthUtil.getMisskeyAccount();

        Pageable<Comment> comments = account.action().getMentionTimeLine(paging);
        printTimeline("Now", comments);
    }
}

