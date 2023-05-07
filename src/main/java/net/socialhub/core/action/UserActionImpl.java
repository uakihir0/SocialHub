package net.socialhub.core.action;

import net.socialhub.core.model.Relationship;
import net.socialhub.core.model.User;

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
    public void follow() {
        action.followUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unfollow() {
        action.unfollowUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mute() {
        action.muteUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unmute() {
        action.unmuteUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void block() {
        action.blockUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unblock() {
        action.unblockUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship getRelationship() {
        return action.getRelationship(user);
    }
}

