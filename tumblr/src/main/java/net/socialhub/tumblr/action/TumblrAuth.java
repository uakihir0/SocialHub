package net.socialhub.tumblr.action;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.JumblrEndpoint;
import net.socialhub.core.action.ServiceAuth;
import net.socialhub.core.define.ServiceType;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Service;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class TumblrAuth implements ServiceAuth<JumblrClient> {

    // For OAuth
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessSecret;

    private Token requestToken;


    public TumblrAuth(
            String consumerKey, //
            String consumerSecret) {

        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    /**
     * Get Request Token for Tumblr
     * Tumblr のリクエストトークンの取得
     */
    @Override
    public JumblrClient getAccessor() {
        JumblrClient client = new JumblrClient(consumerKey, consumerSecret);
        client.setToken(accessToken, accessSecret);
        return client;
    }

    /**
     * Authentication with AccessToken Secret
     * アクセストークンから生成
     */
    public Account getAccountWithAccessToken(
            String accessToken, //
            String accessSecret) {

        this.accessToken = accessToken;
        this.accessSecret = accessSecret;

        Account account = new Account();
        ServiceType type = ServiceType.Tumblr;
        Service service = new Service(type, account);
        account.setAction(new TumblrAction(account, this));
        account.setService(service);
        return account;
    }

    /**
     * Get Authorization URL
     * Tumblr の認証ページの URL を取得
     */
    public String getAuthorizationURL(String callbackUrl) {
        OAuthService service = new ServiceBuilder()
                .provider(JumblrEndpoint.class)
                .apiKey(consumerKey)
                .apiSecret(consumerSecret)
                .callback(callbackUrl)
                .build();

        requestToken = service.getRequestToken();
        return service.getAuthorizationUrl(requestToken);
    }

    /**
     * Authentication with Verifier (or PIN)
     * 認証してアクセストークンを取得し格納
     */
    public Account getAccountWithVerifier(String verifier) {
        OAuthService service = new ServiceBuilder()
                .provider(JumblrEndpoint.class)
                .apiKey(consumerKey)
                .apiSecret(consumerSecret)
                .build();

        Token accessToken = service.getAccessToken(requestToken, new Verifier(verifier));
        return getAccountWithAccessToken(accessToken.getToken(), accessToken.getSecret());
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
