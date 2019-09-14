package net.socialhub.service.action;

import net.socialhub.model.Account;
import net.socialhub.model.error.NotImplimentedException;

public abstract class AccountActionImpl implements AccountAction {

    private Account account;

    @SuppressWarnings("unchecked")
    public <T extends AccountActionImpl> T account(Account account) {
        this.account = account;
        return (T) this;
    }

    public interface ActionCaller<T, E extends Throwable> {
        T proceed() throws E;
    }

    public interface ActionRunner<E extends Throwable> {
        void proceed() throws E;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestAction request() {
        return new RequestActionImpl(account);
    }

    //region // Getter&Setter
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
    //endregion
}
