package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Channel;
import net.socialhub.core.model.Comment;
import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Pageable;
import org.junit.Test;

public class ChannelTimelineTest extends AbstractTimelineTest {

    @Test
    public void testChannelTimeline_Slack() {
        Identify channel = new Identify(null, "CHANNEL_ID");
        Account account = getSlackAccount();

        Pageable<Comment> comments = account.action().getChannelTimeLine(channel, null);

        for (Comment comment : comments.getEntities()) {
            printComment(comment);
        }
    }

    @Test
    public void testChannelTimeline_Bluesky() {
        Account account = getBlueskyAccount();

        Pageable<Channel> channels = account.action().getChannels(null, null);
        Channel channel = channels.getEntities().get(0);

        Pageable<Comment> comments = account.action().getChannelTimeLine(channel, null);

        for (Comment comment : comments.getEntities()) {
            printComment(comment);
        }
    }
}
