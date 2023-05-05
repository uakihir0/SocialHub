package net.socialhub.twitter.model;

import net.socialhub.core.model.service.Channel;
import net.socialhub.core.model.service.Service;
import net.socialhub.core.model.service.User;

public class TwitterChannel extends Channel {

    private User author;

    public TwitterChannel(Service service) {
        super(service);
    }

    // region
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
    // endregion
}
