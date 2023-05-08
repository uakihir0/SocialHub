package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
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

