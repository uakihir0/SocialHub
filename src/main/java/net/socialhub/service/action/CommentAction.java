package net.socialhub.service.action;

import net.socialhub.model.error.NotImplimentedException;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Service;

public interface CommentAction {

    CommentAction comment(Comment comment);

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

    /**
     * Generate CommentAction from Comment
     * コメントからコメントアクションを生成
     */
    static CommentAction of(Comment comment) {
        Service service = comment.getService();
        AccountAction action = service.getAccount().getAction();
        return new CommentActionImpl(action).comment(comment);
    }
}
