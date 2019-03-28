package net.socialhub.model.service;

import net.socialhub.define.ServiceTypeEnum;
import net.socialhub.model.Account;

import java.io.Serializable;

/**
 * SNS サービス情報
 * SNS Service Info
 */
public class Service implements Serializable {

    private Account account;

    private ServiceTypeEnum type;
    private RateLimit rateLimit;

    /** Use Only Mastodon */
    private String apiHost;

    /** Use Only Mastodon */
    private String streamApiHost;

    /**
     * Constructor
     */
    public Service(ServiceTypeEnum type, Account account) {
        this.type = type;
        this.account = account;
        this.rateLimit = new RateLimit();
    }


    /** Is Twitter Account ? */
    public boolean isTwitter() {
        return (ServiceTypeEnum.Twitter == type);
    }

    /** Is Mastodon Account ? */
    public boolean isMastodon() {
        return (ServiceTypeEnum.Mastodon == type);
    }

    /** Is Slack Account ? */
    public boolean isSlack() {
        return (ServiceTypeEnum.Slack == type);
    }

    /** Is Facebook Account ? */
    public boolean isFacebook() {
        return (ServiceTypeEnum.Facebook == type);
    }

    //region // Getter&Setter
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public ServiceTypeEnum getType() {
        return type;
    }

    public void setType(ServiceTypeEnum type) {
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

    public String getStreamApiHost() {
        return streamApiHost;
    }

    public void setStreamApiHost(String streamApiHost) {
        this.streamApiHost = streamApiHost;
    }
    //endregion
}
