package net.socialhub.utils;

import net.socialhub.core.SocialHub;
import net.socialhub.core.define.ServiceType;
import org.junit.Test;

public class ServiceUtilTest {

    @Test
    public void testCommentLengthLevel() {

        {
            String text = "Get Comment Length Level for Post Comment.";
            float level = SocialHub.getUtilServices().getCommentLengthLevel(text, ServiceType.Mastodon);
            System.out.println("Mastodon");
            System.out.println("Text  : " + text);
            System.out.println("Level : " + level);
        }
        {
            String text = "Get Comment Length Level for Post Comment.";
            float level = SocialHub.getUtilServices().getCommentLengthLevel(text, ServiceType.Twitter);
            System.out.println("Twitter");
            System.out.println("Text  : " + text);
            System.out.println("Level : " + level);
        }

    }
}
