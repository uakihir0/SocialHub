package net.socialhub.core.action.request;

import net.socialhub.core.action.RequestActionImpl;
import net.socialhub.core.define.action.ActionType;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.Pageable;
import net.socialhub.core.model.Paging;
import net.socialhub.core.model.User;

import java.util.function.Function;

public class UsersRequestImpl implements UsersRequest {

    private Function<Paging, Pageable<User>> usersFunction;
    private RequestActionImpl.SerializeBuilder serializeBuilder;

    private ActionType actionType;
    private Account account;

    /**
     * {@inheritDoc}
     */
    @Override
    public Pageable<User> getUsers(Paging paging) {
        return usersFunction.apply(paging);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionType getActionType() {
        return actionType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toSerializedString() {
        return getSerializeBuilder().toJson();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account getAccount() {
        return account;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestActionImpl.SerializeBuilder getSerializeBuilder() {
        return serializeBuilder;
    }

    //region // Getter&Setter
    public void setAccount(Account account) {
        this.account = account;
    }

    public void setUsersFunction(Function<Paging, Pageable<User>> usersFunction) {
        this.usersFunction = usersFunction;
    }

    public void setSerializeBuilder(RequestActionImpl.SerializeBuilder serializeBuilder) {
        this.serializeBuilder = serializeBuilder;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
    //endregion
}
