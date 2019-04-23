package net.socialhub.service.twitter;

import net.socialhub.define.ServiceType;
import net.socialhub.model.Account;
import net.socialhub.model.error.SocialHubException;
import net.socialhub.model.service.Service;
import net.socialhub.service.ServiceAuth;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Twitter Authorization Functions
 */
public class    TwitterAuth implements ServiceAuth<Twitter> {

    // For OAuth
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessSecret;

    // For Authorization
    private RequestToken requestToken;

    public TwitterAuth( //
            String consumerKey, //
            String consumerSecret) {

        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    /**
     * Get Request Token for Twitter
     * Twitter のリクエストトークンの取得
     */
    @Override
    public Twitter getAccessor() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        Twitter twitter = new TwitterFactory(builder.build()).getInstance();
        AccessToken token = new AccessToken(accessToken, accessSecret);
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(token);
        return twitter;
    }

    /**
     * Authentication with AccessToken Secret
     * アクセストークンから生成
     */
    public Account getAccountWithAccessToken( //
            String accessToken, //
            String accessSecret) {

        this.accessToken = accessToken;
        this.accessSecret = accessSecret;

        Account account = new Account();
        ServiceType type = ServiceType.Twitter;
        Service service = new Service(type, account);
        account.setAction(new TwitterAction(account, this));
        account.setService(service);
        return account;
    }

    /**
     * Get Authorization URL
     * Twitter の認証ページの URL を取得
     */
    public String getAuthorizationURL(String callbackUrl) {

        ConfigurationBuilder builder = new ConfigurationBuilder();
        Twitter twitter = new TwitterFactory(builder.build()).getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        try {
            requestToken = twitter.getOAuthRequestToken(callbackUrl);
            return requestToken.getAuthorizationURL();

        } catch (TwitterException e) {
            throw new SocialHubException(e);
        }
    }

    /**
     * Authentication with Verifier (or PIN)
     * 認証してアクセストークンを取得し格納
     */
    public Account getAccountWithVerifier(String verifier) {

        ConfigurationBuilder builder = new ConfigurationBuilder();
        Twitter twitter = new TwitterFactory(builder.build()).getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        try {
            AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
            return getAccountWithAccessToken(accessToken.getToken(), accessToken.getTokenSecret());

        } catch (TwitterException e) {
            throw new SocialHubException(e);
        }
    }

    //region // Getter&Setter
    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }
    //endregion
}
