package net.socialhub.utils;

import net.socialhub.service.twitter.TwitterMapper;
import org.junit.Test;

public class TwitterMapperTest {

    @Test
    public void testReplies() {

        System.out.println(TwitterMapper.getRepliesScreenNames(
                "@account1 @account_2 Yeah @account3"));

    }
}
