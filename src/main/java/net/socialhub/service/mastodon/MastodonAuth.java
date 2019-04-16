package net.socialhub.service.mastodon;

import mastodon4j.Mastodon;
import mastodon4j.MastodonFactory;
import mastodon4j.entity.AccessToken;
import mastodon4j.entity.Application;
import mastodon4j.entity.ClientCredential;
import mastodon4j.entity.share.Response;
import net.socialhub.define.ServiceTypeEnum;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.service.Service;
import net.socialhub.service.ServiceAuth;

/**
 * Mastodon Authorization Functions
 */
public class MastodonAuth implements ServiceAuth<Mastodon> {

    /** Show token on Mastodon WebUI */
    public static final String REDIRECT_NONE = "urn:ietf:wg:oauth:2.0:oob";

    private static Logger logger = Logger.getLogger(MastodonAuth.class);

    private String host;
    private String clientId;
    private String clientSecret;
    private String accessToken;

    public MastodonAuth(String host) {
        this.host = host;
    }

    /**
     * Get Request Token for Mastodon
     * Mastodon のリクエストトークンの取得
     */
    @Override
    public Mastodon getAccessor() {
        return MastodonFactory.getInstance( //
                this.host, this.accessToken);
    }

    /**
     * Authentication with AccessToken
     * アクセストークンから生成
     */
    public Account getAccountWithAccessToken(String accessToken) {

        this.accessToken = accessToken;
        Account account = new Account();
        ServiceTypeEnum type = ServiceTypeEnum.Mastodon;
        Service service = new Service(type, account);
        account.setAction(new MastodonAction(account, this));
        account.setService(service);
        return account;
    }

    /**
     * Set Client Info
     * 申請済みクライアント情報を設定
     */
    public void setClientInfo( //
            String clientId, //
            String clientSecret) {

        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Request Client Application
     * クライアント情報を申請して設定
     */
    public void requestClientApplication( //
            String appName, //
            String website, //
            String redirectUris, //
            String scopes) {

        Application application = new Application();
        application.setName(appName);
        application.setWebsite(website);

        Mastodon mastodon = MastodonFactory.getInstance(this.host, null);
        Response<ClientCredential> credential = mastodon.apps() //
                .registerApplication(application, redirectUris, scopes);

        this.clientId = credential.get().getClientId();
        this.clientSecret = credential.get().getClientSecret();
    }

    /**
     * Get Authorization URL
     * Mastodon の認証ページの URL を取得
     */
    public String getAuthorizationURL( //
            String redirectUri, //
            String scopes) {

        Mastodon mastodon = MastodonFactory.getInstance(this.host, null);
        return mastodon.getAuthorizationUrl(this.clientId, redirectUri, scopes);
    }

    /**
     * Authentication with Code
     * 認証コードよりアカウントモデルを生成
     */
    public Account getAccountWithCode( //
            String redirectUri, //
            String code) {

        Response<AccessToken> accessToken = //
                MastodonFactory.getInstance(this.host, null).oauth() //
                        .issueAccessToken(this.clientId, this.clientSecret, redirectUri, code);

        return getAccountWithAccessToken(accessToken.get().getAccessToken());
    }

    //region // Getter&Setter
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    //endregion
}
