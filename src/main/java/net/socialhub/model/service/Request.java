package net.socialhub.model.service;

import net.socialhub.define.action.ActionType;
import net.socialhub.model.Account;

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
