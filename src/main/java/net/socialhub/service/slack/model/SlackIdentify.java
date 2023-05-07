package net.socialhub.service.slack.model;

import net.socialhub.core.model.Identify;
import net.socialhub.core.model.Service;

public class SlackIdentify extends Identify {

    private String channel;

    public SlackIdentify(Service service) {
        super(service);
    }

    public SlackIdentify(Service service, Object id) {
        super(service, id);
    }

    //region // Getter&Setter
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
    //endregion
}
