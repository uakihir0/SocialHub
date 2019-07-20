package net.socialhub.apis;

import net.socialhub.SocialAuthUtil;
import net.socialhub.define.EmojiType;
import net.socialhub.model.Account;
import net.socialhub.model.service.addition.slack.SlackIdentify;
import org.junit.Ignore;
import org.junit.Test;

public class AddReactionTest extends AbstractApiTest {

    @Test
    @Ignore
    public void testAddReactionSlack() {

        Account account = SocialAuthUtil.getSlackAccount();
        SlackIdentify identify = new SlackIdentify(account.getService());
        identify.setChannel("CHANNEL_ID");
        identify.setId("TIME_STAMP");

        account.action().reactionComment(identify, EmojiType.ThinkingFace.getName());
    }
}
