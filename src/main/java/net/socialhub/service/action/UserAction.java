package net.socialhub.service.action;

import net.socialhub.model.error.NotImplimentedException;

public interface UserAction {

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
}
