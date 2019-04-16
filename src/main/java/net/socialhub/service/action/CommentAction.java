package net.socialhub.service.action;

import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Service;

public interface CommentAction {

    /**
     * Add Reaction
     * リアクションをする
     */
    default void reaction(String action) {
        throw new NotImplimentedException();
    }

    /**
     * Remove Reaction
     * リアクションをする
     */
    default void unreaction(String action) {
        throw new NotImplimentedException();
    }

    /**
     * Like Comment
     * コメントをいいねする
     */
    default void like() {
        throw new NotImplimentedException();
    }

    /**
     * UnLike Comment
     * コメントのいいねを消す
     */
    default void unlike() {
        throw new NotImplimentedException();
    }

    /**
     * Share Comment
     * コメントをシェアする
     */
    default void share() {
        throw new NotImplimentedException();
    }

    /**
     * Unshare Comment
     * コメントのシェアを取り消す
     */
    default void unshare() {
        throw new NotImplimentedException();
    }

    // ============================================================== //
    // Alias
    // エイリアス
    // ============================================================== //

    /** Like <-> Favorite */
    default void favorite() {
        like();
    }

    default void unfavorite() {
        unlike();
    }

    /** Share <-> Retweet */
    default void retweet() {
        share();
    }

    default void unretweet() {
        unshare();
    }
}
