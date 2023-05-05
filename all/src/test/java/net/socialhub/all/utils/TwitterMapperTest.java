package net.socialhub.all.utils;

import net.socialhub.twitter.action.TwitterMapper;
import org.junit.Test;

public class TwitterMapperTest {

    @Test
    public void testReplies() {

        System.out.println(TwitterMapper.getRepliesScreenNames(
                "@account1 @account_2 Yeah @account3"));

    }
}
