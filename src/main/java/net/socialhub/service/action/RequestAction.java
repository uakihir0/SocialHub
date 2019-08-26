package net.socialhub.service.action;

import net.socialhub.define.action.ActionType;
import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.Stream;
import net.socialhub.model.service.User;
import net.socialhub.service.action.callback.EventCallback;

public interface RequestAction {

    /**
     * Get Comments
     * コメントを取得
     */
    Pageable<Comment> getComments(Paging paging);

    /**
     * Get Users
     * ユーザーを取得
     */
    Pageable<User> getUsers(Paging paging);

    /**
     * Set Comment Stream Event Callback
     * コメント関連のコールバックを設定
     */
    Stream setCommentsStream(EventCallback callback);

    static RequestAction of(AccountAction action, ActionType type, Object... args) {
        return new RequestActionImpl(action, type, args);
    }
}
