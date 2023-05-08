package net.socialhub.core.action;

import net.socialhub.core.model.Relationship;
import net.socialhub.core.model.User;

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
    void follow();

    /**
     * UnFollow User
     * アカウントをアンフォロー
     */
    void unfollow();

    /**
     * Mute User
     * ユーザーをミュート
     */
    void mute();

    /**
     * UnMute User
     * ユーザーをミュート解除
     */
    void unmute();

    /**
     * Block User
     * ユーザーをブロック
     */
    void block();

    /**
     * UnBlock User
     * ユーザーをブロック解除
     */
    void unblock();

    /**
     * Get relationship
     * 認証アカウントとの関係を取得
     */
    Relationship getRelationship();
}
