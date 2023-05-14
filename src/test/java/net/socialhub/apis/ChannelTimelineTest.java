package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Pageable;
import org.junit.Test;

public class ChannelTimelineTest extends AbstractApiTest {

    @Test
    public void testChannelTimeline() {
        Identify channel = new Identify(null, "CHANNEL_ID");
        Account account = getSlackAccount();

        Pageable<Comment> comments = account.action().getChannelTimeLine(channel, null);

        for (Comment comment : comments.getEntities()) {
            System.out.println(comment.getId() + ":" + comment.getText().getDisplayText());
            System.out.println(comment.getUser().getName());
        }
    }
}
