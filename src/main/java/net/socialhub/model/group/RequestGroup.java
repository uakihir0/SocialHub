package net.socialhub.model.group;

import net.socialhub.define.action.ActionType;
import net.socialhub.model.Account;
import net.socialhub.service.action.RequestAction;
import net.socialhub.service.action.group.RequestGroupAction;

import java.util.Map;

public interface RequestGroup {

    static RequestGroup of() {
        return new RequestGroupImpl();
    }

    /**
     * Add Account and Action
     * アカウントとアクションの追加
     */
    void addAccount(Account account, ActionType type, Object... args);

    /**
     * Return Actions related to Accounts
     * アカウントに紐づくクエリアクションを返す
     */
    Map<Account, RequestAction> getActions();

    RequestGroupAction action();
}
