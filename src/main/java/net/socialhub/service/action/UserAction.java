package net.socialhub.service.action;

import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;

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
    default void unfollowUser() {
        throw new IllegalStateException();
    }

    /**
     * Mute User
     * ユーザーをミュート
     */
    default void muteUser() {
        throw new NotImplimentedException();
    }

    /**
     * UnMute User
     * ユーザーをミュート解除
     */
    default void unmuteUser() {
        throw new NotImplimentedException();
    }

    /**
     * Block User
     * ユーザーをブロック
     */
    default void blockUser() {
        throw new NotImplimentedException();
    }

    /**
     * UnBlock User
     * ユーザーをブロック解除
     */
    default void unblockUser() {
        throw new NotImplimentedException();
    }

    /**
     * Generate UserAction from User
     * ユーザーからユーザーアクションを生成
     */
    static UserAction of(User user) {
        Service service = user.getService();
        AccountAction action = service.getAccount().getAction();
        return new UserActionImpl(action).user(user);
    }
}
