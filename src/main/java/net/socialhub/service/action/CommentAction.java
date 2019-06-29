package net.socialhub.service.action;

import net.socialhub.model.service.Comment;

public interface CommentAction {

    /**
     * Get Comment
     * コメントを再度取得
     */
    Comment refresh();

    /**
     * Add Reaction
     * リアクションをする
     */
    void reaction(String action);

    /**
     * Remove Reaction
     * リアクションをする
     */
    void unreaction(String action);

    /**
     * Like Comment
     * コメントをいいねする
     */
    void like();

    /**
     * UnLike Comment
     * コメントのいいねを消す
     */
    void unlike();

    /**
     * Share Comment
     * コメントをシェアする
     */
    void share();

    /**
     * Unshare Comment
     * コメントのシェアを取り消す
     */
    void unshare();

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
