package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Comment;
import net.socialhub.core.model.service.Identify;
import org.junit.Test;

public class GetCommentTest extends AbstractTimelineTest {

    @Test
    public void testTwitterGetComment() {
        Long id = 0L;
        Identify identify = new Identify(null, id);

        Account account = SocialAuthUtil.getTwitterAccount();
        Comment comment = account.action().getComment(identify);
        printComment(comment);
    }
}
