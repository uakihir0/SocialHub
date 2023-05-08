package net.socialhub.service.twitter.model;

import net.socialhub.core.model.Channel;
import net.socialhub.core.model.Service;
import net.socialhub.core.model.User;

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
