package net.socialhub.service.action;

import net.socialhub.model.service.User;

public interface UserAction {

    /**
     * Get Account
     * アカウントを再度取得
     */
    User refresh();

    /**
     * Follow User
     * アカウントをフォロー
     */
    void followUser();

    /**
     * UnFollow User
     * アカウントをアンフォロー
     */
    void unfollowUser();

    /**
     * Mute User
     * ユーザーをミュート
     */
    void muteUser();

    /**
     * UnMute User
     * ユーザーをミュート解除
     */
    void unmuteUser();

    /**
     * Block User
     * ユーザーをブロック
     */
    void blockUser();

    /**
     * UnBlock User
     * ユーザーをブロック解除
     */
    void unblockUser();
}
