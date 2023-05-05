package net.socialhub.core.model.group;

import net.socialhub.core.model.Account;
import net.socialhub.core.action.group.AccountGroupAction;

import java.util.List;

/**
 * Account Group Model
 * 複数のアカウントを束ねるモデル
 */
public interface AccountGroup {

    static AccountGroup of(Account... accounts) {
        return new AccountGroupImpl(accounts);
    }

    /**
     * Get Accounts List
     * アカウントリストの追加
     */
    List<Account> getAccounts();

    /**
     * Add Accounts
     * アカウントの追加
     */
    void addAccount(Account account);

    /**
     * Get Account Actions
     * アクションの取得
     */
    AccountGroupAction action();
}
