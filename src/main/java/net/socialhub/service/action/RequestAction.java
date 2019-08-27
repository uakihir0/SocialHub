package net.socialhub.service.action;

import net.socialhub.define.action.ActionType;
import net.socialhub.model.service.*;
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

    /**
     * Get Action Type
     */
    ActionType getActionType();

    /**
     * Make Request Action Instance
     */
    static RequestAction of(AccountAction action, ActionType type, Object... args) {
        return new RequestActionImpl(action, type, args);
    }
}
