package net.socialhub.service.action;

import net.socialhub.model.service.User;

public class SuperUserAction implements UserAction {

    private AccountAction action;
    private User user;

    public SuperUserAction(AccountAction action) {
        this.action = action;
    }

    public UserAction user(User user) {
        this.user = user;
        return this;
    }

    @Override
    public void followUser() {
        action.followUser(user);
    }
}

