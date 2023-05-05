package net.socialhub.misskey.action;

import misskey4j.Misskey;
import misskey4j.MisskeyFactory;
import misskey4j.api.request.CreateAppRequest;
import misskey4j.api.request.GenerateAuthSessionRequest;
import misskey4j.api.request.UserKeyAuthSessionRequest;
import misskey4j.api.response.CreateAppResponse;
import misskey4j.api.response.GenerateAuthSessionResponse;
import misskey4j.api.response.UserKeyAuthSessionResponse;
import misskey4j.entity.share.Response;
import net.socialhub.core.define.ServiceType;
import net.socialhub.logger.Logger;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.Service;
import net.socialhub.core.action.ServiceAuth;

import java.util.List;

/**
 * Misskey Authorization Functions
 */
public class MisskeyAuth implements ServiceAuth<Misskey> {

    private static Logger logger = Logger.getLogger(MisskeyAuth.class);

    private String host;
    private String clientId;
    private String clientSecret;
    private String accessToken;

    public MisskeyAuth(String host) {
        this.host = host;
    }

    /**
     * Get Request Token for Misskey
     * Mastodon のリクエストトークンの取得
     */
    @Override
    public Misskey getAccessor() {
        if ((clientSecret != null) && (accessToken != null)) {
            return MisskeyFactory.getInstance(host, clientSecret, accessToken);
        }
        return MisskeyFactory.getInstance(host);
    }

    /**
     * Authentication with AccessToken
     * アクセストークンから生成
     */
    public Account getAccountWithAccessToken(String accessToken) {

        this.accessToken = accessToken;
        Account account = new Account();

        ServiceType type = ServiceType.Misskey;
        Service service = new Service(type, account);
        service.setApiHost(host);

        account.setAction(new MisskeyAction(account, this));
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
            String appName, //
            String description, //
            String callbackUrl, //
            List<String> permissions) {

        CreateAppRequest request =
                CreateAppRequest.builder()
                        .name(appName)
                        .description(description)
                        .permission(permissions)
                        .callbackUrl(callbackUrl)
                        .build();

        Misskey misskey = MisskeyFactory.getInstance(host);
        Response<CreateAppResponse> response = misskey.app().createApp(request);

        this.clientId = response.get().getId();
        this.clientSecret = response.get().getSecret();
    }

    /**
     * Get Authorization URL
     * Misskey の認証ページの URL を取得
     */
    public String getAuthorizationURL() {

        Misskey misskey = MisskeyFactory.getInstance(host);
        Response<GenerateAuthSessionResponse> response =
                misskey.auth().sessionGenerate(
                        GenerateAuthSessionRequest.builder()
                                .appSecret(clientSecret)
                                .build());

        return response.get().getUrl();
    }

    /**
     * Authentication with Code
     * 認証コードよりアカウントモデルを生成
     */
    public Account getAccountWithCode(
            String verifier) {

        Misskey misskey = MisskeyFactory.getInstance(host);
        Response<UserKeyAuthSessionResponse> response =
                misskey.auth().sessionUserKey(
                        UserKeyAuthSessionRequest.builder()
                                .appSecret(clientSecret)
                                .token(verifier)
                                .build());

        return getAccountWithAccessToken(response.get().getAccessToken());
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
