package net.socialhub.service.action;

import net.socialhub.model.service.Comment;

public interface CommentAction {

    CommentAction comment(Comment comment);

    /**
     * Like Comment
     * コメントをいいねする
     */
    default void like() {
        throw new IllegalStateException();
    }

    /**
     * UnLike Comment
     * コメントのいいねを消す
     */
    default void unlike() {
        throw new IllegalStateException();
    }

}
