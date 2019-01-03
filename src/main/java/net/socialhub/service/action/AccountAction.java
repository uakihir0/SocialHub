package net.socialhub.service.action;

import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.service.*;

/**
 * Account Actions
 * (全てのアクションを定義)
 */
public interface AccountAction {

    // ============================================================== //
    // Account API
    // アカウント関連 API
    // ============================================================== //

    /**
     * Get Authorized My Account
     * 認証した自身のユーザー情報を取得
     */
    default User getUserMe() {
        throw new NotImplimentedException();
    }

    /**
     * Get Specific UserInfo
     * 特定のユーザーを取得
     */
    default User getUser(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Follow User
     * ユーザーをフォロー
     */
    default void followUser(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Unfollow User
     * ユーザーをフォロー解除
     */
    default void unfollowUser(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Mute User
     * ユーザーをミュート
     */
    default void muteUser(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Unmute User
     * ユーザーをミュート解除
     */
    default void unmuteUser(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Block User
     * ユーザーをブロック
     */
    default void blockUser(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Unblock User
     * ユーザーをブロック解除
     */
    default void unblockUser(Identify id) {
        throw new NotImplimentedException();
    }


    // ============================================================== //
    // Comment API
    // コメント関連 API
    // ============================================================== //

    /**
     * Get Home TimeLine
     * タイムラインを取得
     */
    default Pageable<Comment> getHomeTimeLine(Paging paging) {
        throw new NotImplimentedException();
    }

    /**
     * Like Comment
     * コメントにたいしてイイねする
     * (Twitter Mastodon ではお気に入りをする)
     */
    default void like(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Unlike Comment
     * コメントに対してのイイねを取り消す
     * (Twitter Mastodon ではお気に入りを消す)
     */
    default void unlike(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Share Comment
     * コメントをシェアする
     */
    default void share(Identify id) {
        throw new NotImplimentedException();
    }

    /**
     * Unshare Comment
     * コメントのシェアを取り消す
     */
    default void unshare(Identify id) {
        throw new NotImplimentedException();
    }


    // ============================================================== //
    // Alias
    // エイリアス
    // ============================================================== //

    /** Like <-> Favorite */
    default void favorite(Identify id) {
        like(id);
    }

    default void unfavorite(Identify id) {
        unlike(id);
    }

    /** Share <-> Retweet */
    default void retweet(Identify id) {
        share(id);
    }

    default void unretweet(Identify id) {
        unshare(id);
    }
}
