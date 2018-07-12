package net.socialhub;

import org.junit.Test;

public class TestProperty {

    @Test
    public void testCompilable() {
        System.out.print("DONE");
    }


    public static void before() {
    }

    public static class TwitterProperty {

        public static final String ConsumerKey = "";
        public static final String ConsumerSecret = "";

        public static final String AccessToken = "";
        public static final String AccessSecret = "";
    }

    public static class FacebookProperty {

        public static final String AppId = "";
        public static final String AppSecret = "";

        public static final String AccessToken = "";
    }

    public static class MastodonProperty {

        public static final String Host = "";
        public static final String ClientId = "";
        public static final String ClientSecret = "";
        public static final String AccessToken = "";
    }

    public static class SlackProperty {

        public static final String ClientId = "";
        public static final String ClientSecret = "";
        public static final String Token = "";
    }
}
