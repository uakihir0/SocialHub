package net.socialhub.service.action;

import net.socialhub.model.service.User;

public class UserActionImpl implements UserAction {

    private AccountAction action;
    private User user;

    public UserActionImpl(AccountAction action) {
        this.action = action;
    }

    public UserAction user(User user) {
        this.user = user;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User refresh() {
        return action.getUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void followUser() {
        action.followUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unfollowUser() {
        action.unfollowUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void muteUser() {
        action.muteUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmuteUser() {
        action.unmuteUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void blockUser() {
        action.muteUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unblockUser() {
        action.unmuteUser(user);
    }
}

