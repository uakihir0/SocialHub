package net.socialhub.service.slack;

import com.github.seratch.jslack.Slack;
import net.socialhub.define.ServiceTypeEnum;
import net.socialhub.model.Account;
import net.socialhub.model.service.Service;
import net.socialhub.service.ServiceAuth;
import net.socialhub.service.slack.SlackAuth.SlackAccessor;

public class SlackAuth implements ServiceAuth<SlackAccessor> {

    private String clientId;
    private String clientSecret;
    private SlackAccessor accessor;

    public SlackAuth(String clientId,
                     String clientSecret) {

        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Authentication with AccessToken / AccessSecret
     * アクセストークンから生成
     */
    public Account getAccountWithToken(String token) {
        this.accessor = new SlackAccessor();
        this.accessor.setSlack(Slack.getInstance());
        this.accessor.setToken(token);

        Account account = new Account();
        ServiceTypeEnum type = ServiceTypeEnum.Slack;
        Service service = new Service(type, account);
        account.setAction(new SlackAction(account, this));
        account.setService(service);
        return account;
    }

    @Override
    public SlackAccessor getToken() {
        return accessor;
    }

    /**
     * Slack Accessor
     * (Slack Instance And Token)
     */
    public static class SlackAccessor {
        private String token;
        private Slack slack;

        //region // Getter&Setter
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Slack getSlack() {
            return slack;
        }

        public void setSlack(Slack slack) {
            this.slack = slack;
        }
        //endregion
    }
}
