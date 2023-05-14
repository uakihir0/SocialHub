package net.socialhub.service.mastodon.action;

import mastodon4j.Mastodon;
import mastodon4j.MastodonFactory;
import mastodon4j.entity.AccessToken;
import mastodon4j.entity.Application;
import mastodon4j.entity.ClientCredential;
import mastodon4j.entity.share.Response;
import net.socialhub.core.action.ServiceAuth;
import net.socialhub.core.define.ServiceType;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Service;
import net.socialhub.logger.Logger;

import java.util.Date;

/**
 * Mastodon Authorization Functions
 */
public class MastodonAuth implements ServiceAuth<Mastodon> {

    /** Show token on Mastodon WebUI */
    public static final String REDIRECT_NONE = "urn:ietf:wg:oauth:2.0:oob";

    private static final Logger logger = Logger.getLogger(MastodonAuth.class);

    private String host;
    private String clientId;
    private String clientSecret;
    private String accessToken;
    private String refreshToken;
    private ServiceType type;
    private Date expired;

    public MastodonAuth(String host, ServiceType type) {
        this.host = host;
        this.type = type;
    }

    /**
     * Get Request Token for Mastodon
     * Mastodon のリクエストトークンの取得
     */
    @Override
    public Mastodon getAccessor() {
        return MastodonFactory.getInstance( //
                getService(), this.host, this.accessToken);
    }

    /**
     * Authentication with AccessToken
     * アクセストークンから生成
     */
    public Account getAccountWithAccessToken(
            String accessToken,
            String refreshToken,
            Date expired) {

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expired = expired;

        Account account = new Account();
        ServiceType type = (this.type != null)
                ? this.type : ServiceType.Mastodon;
        Service service = new Service(type, account);
        service.setApiHost(host);

        account.setAction(new MastodonAction(account, this));
        account.setService(service);
        return account;
    }

    /**
     * Set Client Info
     * 申請済みクライアント情報を設定
     */
    public void setClientInfo(
            String clientId,
            String clientSecret) {

        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Request Client Application
     * クライアント情報を申請して設定
     */
    public void requestClientApplication(
            String appName,
            String website,
            String redirectUris,
            String scopes) {

        Application application = new Application();
        application.setName(appName);
        application.setWebsite(website);

        Response<ClientCredential> credential = getAccessor().apps()
                .registerApplication(application, redirectUris, scopes);

        this.clientId = credential.get().getClientId();
        this.clientSecret = credential.get().getClientSecret();
    }

    /**
     * Get Authorization URL
     * Mastodon の認証ページの URL を取得
     */
    public String getAuthorizationURL(
            String redirectUri,
            String scopes
    ) {
        return getAccessor().getAuthorizationUrl(
                this.clientId, redirectUri, scopes);
    }

    /**
     * Authentication with Code
     * 認証コードよりアカウントモデルを生成
     */
    public Account getAccountWithCode(
            String redirectUri,
            String code) {

        Response<AccessToken> accessToken = getAccessor().oauth()
                .issueAccessToken(this.clientId, this.clientSecret, redirectUri, code);

        return getAccountWithAccessToken(
                accessToken.get().getAccessToken(),
                accessToken.get().getRefreshToken(),
                getExpireAt(accessToken.get().getExpiresIn())
        );
    }

    /**
     * Refresh AccessToken with RefreshToken.
     * トークン情報を更新
     */
    public Account refreshToken() {
        Response<AccessToken> accessToken = getAccessor().oauth()
                .refreshAccessToken(this.clientId, this.clientSecret, this.refreshToken);

        return getAccountWithAccessToken(
                accessToken.get().getAccessToken(),
                accessToken.get().getRefreshToken(),
                getExpireAt(accessToken.get().getExpiresIn())
        );
    }

    private Date getExpireAt(Long expireInSec) {
        return (expireInSec == null) ? null :
                new Date(System.currentTimeMillis() + (expireInSec * 1000));
    }

    private mastodon4j.domain.Service getService() {
        if (type == ServiceType.Mastodon) {
            return mastodon4j.domain.Service.MASTODON;
        }
        if (type == ServiceType.PixelFed) {
            return mastodon4j.domain.Service.PIXELFED;
        }
        if (type == ServiceType.Pleroma) {
            return mastodon4j.domain.Service.PLEROMA;
        }
        return null;
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

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }
    //endregion
}
