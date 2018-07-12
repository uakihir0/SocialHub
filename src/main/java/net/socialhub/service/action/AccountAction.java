package net.socialhub.service.action;

import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.service.*;

public interface AccountAction {

    // ============================================================== //
    // Account
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
     * UnFollow User
     * ユーザーをアンフォロー
     */
    default void unfollowUser(Identify id) {
        throw new NotImplimentedException();
    }

    // ============================================================== //
    // Comment
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
     * UnLike Comment
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
    default void share(Identify id){
        throw new NotImplimentedException();
    }

    /**
     * UnShare Comment
     * コメントのシェアを取り消す
     */
    default void unshare(Identify id){
        throw new NotImplimentedException();
    }


    // ============================================================== //
    // Alias
    // ============================================================== //

    /** Like <-> Favorite */
    default void favorite(Identify id){
        like(id);
    }
    default void unfavorite(Identify id){
        unlike(id);
    }

    /** Share <-> Retweet */
    default void retweet(Identify id){
        share(id);
    }
    default void unretweet(Identify id){
        unshare(id);
    }
}
