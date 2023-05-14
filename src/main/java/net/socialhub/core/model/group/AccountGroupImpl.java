package net.socialhub.core.model.group;

import net.socialhub.core.action.group.AccountGroupAction;
import net.socialhub.core.action.group.AccountGroupActionImpl;
import net.socialhub.core.model.Account;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountGroupImpl implements AccountGroup {

    private List<Account> accounts = new ArrayList<>();

    /**
     * コンストラクタ
     */
    public AccountGroupImpl(Account... accounts) {
        if (accounts != null && accounts.length > 0) {
            this.accounts.addAll(Arrays.asList(accounts));
        }
    }

    @Override
    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    @Override
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
