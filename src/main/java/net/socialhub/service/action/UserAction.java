package net.socialhub.service.action;

import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;
import net.socialhub.service.facebook.FacebookAction.SHFacebookUserAction;

public interface UserAction {

    UserAction user(User user);

    /**
     * Follow User
     * アカウントをフォロー
     */
    default void followUser() {
        throw new IllegalStateException();
    }

    /**
     * UnFollow User
     * アカウントをアンフォロー
     */
    default void unfollowUser(){
        throw new IllegalStateException();
    }

    static UserAction of(User user) {
        Service service = user.getService();
        AccountAction action = service.getAccount().getAction();

        switch (service.getService()) {
            case Facebook:
                return new SHFacebookUserAction(action).user(user);
        }

        return new SuperUserAction(action).user(user);
    }
}
