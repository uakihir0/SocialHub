package net.socialhub.model.service.addition.twitter;

import net.socialhub.model.service.Channel;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;

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
