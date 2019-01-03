package net.socialhub.service.mastodon;

import mastodon4j.Mastodon;
import mastodon4j.MastodonFactory;
import net.socialhub.define.ServiceTypeEnum;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.service.Service;
import net.socialhub.service.ServiceAuth;

public class MastodonAuth implements ServiceAuth<Mastodon> {

    private static Logger logger = Logger.getLogger(MastodonAuth.class);

    private String host;
    private String clientId;
    private String clientSecret;
    private String accessToken;


    public MastodonAuth(String host) {
        this.host = host;
    }

    /**
     * クライアント情報を設定
     * Set Client Info
     */
    public void setClientInfo(String clientId,
                              String clientSecret) {

        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Authentication with AccessToken / AccessSecret
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
     * Get Request Token for Mastodon
     * Mastodon のリクエストトークンの取得
     */
    @Override
    public Mastodon getToken() {
        return MastodonFactory.getInstance(
                this.host, this.accessToken);
    }
}
