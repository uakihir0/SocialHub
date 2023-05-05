package net.socialhub.core.model.service.event;

import net.socialhub.core.model.service.User;

public class UserEvent {

    private User user;

    public UserEvent(User user) {
        this.user = user;
    }

    //region // Getter&Setter
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    //endregion
}
