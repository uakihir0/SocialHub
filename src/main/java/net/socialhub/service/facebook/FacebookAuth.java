package net.socialhub.service.facebook;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;
import facebook4j.conf.ConfigurationBuilder;
import net.socialhub.define.ServiceType;
import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.model.service.Service;
import net.socialhub.service.ServiceAuth;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FacebookAuth implements ServiceAuth<Facebook> {

    private static Logger logger = Logger.getLogger(FacebookAuth.class);


    private String appId;
    private String appSecret;

    private String accessToken;
    private Date accessTokenExpired;

    private List<String> permissions;

    public FacebookAuth(String appId,
                        String appSecret) {

        this.appId = appId;
        this.appSecret = appSecret;
    }

    /**
     * Set Permissions
     * パーミッションの設定
     */
    public FacebookAuth addPermission(String... permission) {
        if (permissions == null) permissions = new ArrayList<>();
        Collections.addAll(permissions, permission);
        return this;
    }

    /**
     * Set AccessToken
     * アクセストークンを設定
     */
    public FacebookAuth accessToken(String accessToken) {
        if (accessToken != null)
            this.accessToken = accessToken;
        return this;
    }

    /**
     * Authentication with AccessToken / AccessSecret
     * アクセストークンから生成
     */
    public Account getAccountWithAccessToken() {
        Account account = new Account();
        ServiceType type = ServiceType.Facebook;
        Service service = new Service(type, account);
        account.setAction(new FacebookAction(account, this));
        account.setService(service);
        return account;
    }

    public Account getAccountWithAccessToken(String accessToken) {
        return this.accessToken(accessToken).getAccountWithAccessToken();
    }

    /**
     * Get Access Token
     * アクセストークンの取得
     */
    public AccessToken getOAuthAccessToken(String oauthCode, String callbackUrl) {
        try {
            return getAccessor().getOAuthAccessToken(oauthCode, callbackUrl);
        } catch (FacebookException e) {
            handleFacebookException(e);
            return null;
        }
    }

    /**
     * Extend TokenExpiration
     * アクセストークンを拡張
     */
    public void getExtendAccessToken() {
        try {
            AccessToken token = getAccessor().extendTokenExpiration();
            this.accessTokenExpired = new Date(token.getExpires() * 1000);
            this.accessToken = token.getToken();

        } catch (FacebookException e) {
            handleFacebookException(e);
        }
        getExtendAccessToken(null);
    }

    public void getExtendAccessToken(String accessToken) {
        this.accessToken(accessToken).getExtendAccessToken();
    }

    /**
     * Get Request Token for Facebook
     * Facebook のリクエストトークンの取得
     */
    public Facebook getAccessor() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        Facebook facebook = new FacebookFactory(builder.build()).getInstance();
        facebook.setOAuthAppId(appId, appSecret);

        if ((permissions != null) && !permissions.isEmpty()) {
            String permission = String.join(",", permissions);
            facebook.setOAuthPermissions(permission);
        }

        if (StringUtils.isNotEmpty(accessToken)) {
            AccessToken token = new AccessToken(accessToken);
            facebook.setOAuthAccessToken(token);
        }

        return facebook;
    }

    private static void handleFacebookException(FacebookException e) {
        logger.debug(e.getMessage(), e);
    }

    //region // Getter&Setter
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public Date getAccessTokenExpired() {
        return accessTokenExpired;
    }

    public void setAccessTokenExpired(Date accessTokenExpired) {
        this.accessTokenExpired = accessTokenExpired;
    }
    //endregion
}
