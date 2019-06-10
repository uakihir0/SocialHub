package net.socialhub.model.group;

import net.socialhub.model.Account;
import net.socialhub.service.action.group.AccountGroupAction;

import java.util.List;

/**
 * Account Group Model
 * 複数のアカウントを束ねるモデル
 */
public interface AccountGroup {

    static AccountGroup of() {
        return new AccountGroupImpl();
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

    AccountGroupAction action();
}
