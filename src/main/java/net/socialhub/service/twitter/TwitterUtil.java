package net.socialhub.service.twitter;

import com.twitter.twittertext.TwitterTextParser;
import net.socialhub.service.Utils;

public class TwitterUtil implements Utils.ServiceUtils {

    @Override
    public float getCommentLengthLevel(String text) {
        return ((float) TwitterTextParser.parseTweet(text).permillage) / 1000.f;
    }
}
