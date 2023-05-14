package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Identify;
import org.junit.Test;

public class GetCommentTest extends AbstractTimelineTest {

    @Test
    public void testTwitterGetComment() {
        Long id = 0L;
        Identify identify = new Identify(null, id);

        Account account = getTwitterAccount();
        Comment comment = account.action().getComment(identify);
        printComment(comment);
    }
}
