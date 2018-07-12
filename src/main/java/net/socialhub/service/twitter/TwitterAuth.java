package net.socialhub.service.twitter;

import net.socialhub.define.ServiceEnum;
import net.socialhub.model.Account;
import net.socialhub.model.service.Service;
import net.socialhub.service.ServiceAuth;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterAuth implements ServiceAuth<Twitter> {

    // For OAuth
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessSecret;

    public TwitterAuth(String consumerKey,
                       String consumerSecret) {

        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    /**
     * Authentication with AccessToken / AccessSecret
     * アクセストークンから生成
     */
    public Account getAccountWithAccessToken(String accessToken,
                                             String accessSecret) {

        this.accessToken = accessToken;
        this.accessSecret = accessSecret;

        Account account = new Account();
        ServiceEnum type = ServiceEnum.Twitter;
        Service service = new Service(type, account);
        account.setAction(new TwitterAction(account, this));
        account.setService(service);
        return account;
    }

    /**
     * Get Request Token for Twitter
     * Twitter のリクエストトークンの取得
     */
    @Override
    public Twitter getToken() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        Twitter twitter = new TwitterFactory(builder.build()).getInstance();
        AccessToken token = new AccessToken(accessToken, accessSecret);
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(token);
        return twitter;
    }
}
