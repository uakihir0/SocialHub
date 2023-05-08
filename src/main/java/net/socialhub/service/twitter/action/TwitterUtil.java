package net.socialhub.service.twitter.action;

import com.twitter.twittertext.TwitterTextParser;
import net.socialhub.core.utils.ServiceUtil;

public class TwitterUtil implements ServiceUtil {

    /**
     * {@inheritDoc}
     */
    @Override
    public float getCommentLengthLevel(String text) {
        return ((float) TwitterTextParser.parseTweet(text).permillage) / 1000.f;
    }
}
