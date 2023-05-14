package net.socialhub.core.model;

import net.socialhub.core.define.action.ActionType;

/**
 * Common Request Interface
 * リクエスト汎用インターフェース
 */
public interface Request {

    /**
     * Get Action Type
     * アクションタイプを取得
     */
    ActionType getActionType();

    /**
     * Get Account Info
     * アカウントを取得
     */
    Account getAccount();
}
