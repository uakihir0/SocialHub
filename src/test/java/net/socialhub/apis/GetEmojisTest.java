package net.socialhub.apis;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.Emoji;
import net.socialhub.logger.Logger;
import org.junit.Test;

import java.util.List;

public class GetEmojisTest extends AbstractApiTest {

    @Test
    public void testGetEmojisTwitter() {
        execGetEmojis(getTwitterAccount());
    }

    @Test
    public void testGetEmojisMastodon() {
        Logger.getLogger(null).setLogLevel(Logger.LogLevel.WARN);
        execGetEmojis(getMastodonAccount());
    }

    @Test
    public void testGetEmojisMisskey() {
        Logger.getLogger(null).setLogLevel(Logger.LogLevel.WARN);
        execGetEmojis(getMisskeyAccount());
    }

    @Test
    public void testGetEmojisSlack() {
        execGetEmojis(getSlackAccount());
    }

    private void execGetEmojis(Account account){
        List<Emoji> emojis = account.action().getEmojis();
        for (Emoji emoji : emojis) {
            printEmoji(emoji);
        }
    }

    private void printEmoji(Emoji emoji) {
        System.out.print(emoji.getShortCode() + ": ");

        if (emoji.getEmoji() != null) {
            System.out.println("E: " + emoji.getEmoji());
        }
        if (emoji.getImageUrl() != null) {
            System.out.println("ICON: " + emoji.getImageUrl());
        }

        List<String> shortCodes = emoji.getShortCodes();

        if (shortCodes.size() > 1) {
            for (String alias : shortCodes) {
                System.out.println("A: " + alias);
            }
        }
    }
}
