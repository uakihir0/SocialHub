package net.socialhub.model.service;

import net.socialhub.define.ServiceType;
import net.socialhub.model.Account;

import java.io.Serializable;

/**
 * SNS サービス情報
 * SNS Service Info
 */
public class Service implements Serializable {

    private Account account;

    private ServiceType type;
    private RateLimit rateLimit;

    /** Use Only Mastodon */
    private String apiHost;

    /**
     * Constructor
     */
    public Service(ServiceType type, Account account) {
        this.type = type;
        this.account = account;
        this.rateLimit = new RateLimit();
    }


    /** Is Twitter Account ? */
    public boolean isTwitter() {
        return (ServiceType.Twitter == type);
    }

    /** Is Mastodon Account ? */
    public boolean isMastodon() {
        return (ServiceType.Mastodon == type);
    }

    /** Is Slack Account ? */
    public boolean isSlack() {
        return (ServiceType.Slack == type);
    }

    /** Is Facebook Account ? */
    public boolean isFacebook() {
        return (ServiceType.Facebook == type);
    }

    /** Is Tumblr Account ? */
    public boolean isTumblr() {
        return (ServiceType.Tumblr == type);
    }

    /** Is Misskey Account ? */
    public boolean isMisskey() {
        return (ServiceType.Misskey == type);
    }

    //region // Getter&Setter
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit;
    }

    public String getApiHost() {
        return apiHost;
    }

    public void setApiHost(String apiHost) {
        this.apiHost = apiHost;
    }
    //endregion
}
