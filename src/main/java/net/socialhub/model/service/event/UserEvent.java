package net.socialhub.model.service.event;

import net.socialhub.model.service.User;

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
