package net.socialhub.model.group;

import net.socialhub.model.Account;
import net.socialhub.service.action.group.AccountGroupAction;
import net.socialhub.service.action.group.AccountGroupActionImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Account Group Model
 * 複数のアカウントを束ねるモデル
 */
public class AccountGroup {

    private List<Account> accounts = new ArrayList<>();

    /**
     * Add account
     * アカウントの追加
     */
    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    public AccountGroupAction action() {
        return new AccountGroupActionImpl(this);
    }

    //region // Getter&Setter
    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
    //endregion
}
