package net.socialhub.model.group;

import net.socialhub.model.Account;

import java.util.ArrayList;
import java.util.List;

/**
 * Account Group Model
 * 複数のアカウントを束ねるモデル
 */
public class GroupAccount {

    private List<Account> accounts = new ArrayList<>();

    /**
     * Add account
     * アカウントの追加
     */
    public void addAccount(Account account) {
        this.accounts.add(account);
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
