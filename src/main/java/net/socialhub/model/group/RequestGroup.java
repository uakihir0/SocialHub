package net.socialhub.model.group;

import net.socialhub.model.Account;
import net.socialhub.model.service.Identify;
import net.socialhub.service.action.RequestAction;
import net.socialhub.service.action.RequestActionImpl;
import net.socialhub.service.action.group.RequestGroupAction;

import java.util.Map;

import static net.socialhub.define.ActionType.*;

public interface RequestGroup {

    static RequestGroup of() {
        return new RequestGroupImpl();
    }

    /**
     * Add Account and Action
     * アカウントとアクションの追加
     */
    void addAccount(Account account, RequestAction action);


    default void addHomeTimeLine(Account account) {
        addAccount(account, new RequestActionImpl(account.action(), HomeTimeLine));
    }

    default void addMentionTimeLine(Account account) {
        addAccount(account, new RequestActionImpl(account.action(), MentionTimeLine));
    }

    default void addUserCommentTimeLine(Account account, Identify id) {
        addAccount(account, new RequestActionImpl(account.action(), UserCommentTimeLine, id));
    }

    default void addUserLikeTimeLine(Account account, Identify id) {
        addAccount(account, new RequestActionImpl(account.action(), UserLikeTimeLine, id));
    }

    /**
     * Return Actions related to Accounts
     * アカウントに紐づくクエリアクションを返す
     */
    Map<Account, RequestAction> getActions();

    RequestGroupAction action();
}
