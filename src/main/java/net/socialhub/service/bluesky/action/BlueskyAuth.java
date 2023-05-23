package net.socialhub.service.bluesky.action;

import bsky4j.Bluesky;
import bsky4j.BlueskyFactory;
import net.socialhub.core.action.ServiceAuth;
import net.socialhub.core.define.ServiceType;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Service;
import net.socialhub.logger.Logger;

import java.net.URL;

public class BlueskyAuth implements ServiceAuth<Bluesky> {

    private static final Logger logger = Logger.getLogger(BlueskyAuth.class);

    private Bluesky bluesky;
    private String host;
    private String identifier;
    private String password;

    public BlueskyAuth(String host) {
        if (!host.startsWith("http")) {
            host = "https://" + host;
        }
        try {
            URL url = new URL(host);
            this.host = url.getProtocol() + "://"
                    + url.getHost() + "/";

        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid host: " + host);
        }
    }

    /**
     * Get Access Token for Bluesky
     * Bluesky のアクセストークンの取得
     */
    public Account getAccountWithIdentifyAndPassword(
            String identifier,
            String password
    ) {
        this.identifier = identifier;
        this.password = password;
        this.bluesky = BlueskyFactory.getInstance(this.host);

        Account account = new Account();
        ServiceType type = ServiceType.Bluesky;
        Service service = new Service(type, account);
        service.setApiHost(this.host);

        account.setAction(new BlueskyAction(account, this));
        account.setService(service);
        return account;
    }


    @Override
    public Bluesky getAccessor() {
        return bluesky;
    }

    // region
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    // endregion
}
