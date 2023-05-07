package net.socialhub.core.model.event;

import net.socialhub.core.model.User;

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
