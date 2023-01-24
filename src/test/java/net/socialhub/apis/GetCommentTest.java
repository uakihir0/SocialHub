package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.model.Account;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
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
