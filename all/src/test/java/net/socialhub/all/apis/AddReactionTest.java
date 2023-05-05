package net.socialhub.all.apis;

import net.socialhub.all.SocialAuthUtil;
import net.socialhub.core.define.emoji.EmojiType;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Identify;
import net.socialhub.slack.model.SlackIdentify;
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

    @Test
    @Ignore
    public void testAddReactionTwitter() {

        Account account = SocialAuthUtil.getTwitterAccount();
        Identify identify = new SlackIdentify(account.getService());
        identify.setId(0L);

        account.action().favoriteComment(identify);
    }
}
