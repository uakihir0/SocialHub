package net.socialhub.service.action.request;

import net.socialhub.define.action.ActionType;
import net.socialhub.model.Account;
import net.socialhub.model.service.Pageable;
import net.socialhub.model.service.Paging;
import net.socialhub.model.service.User;
import net.socialhub.service.action.RequestActionImpl.SerializeBuilder;

import java.util.function.Function;

public class UsersRequestImpl implements UsersRequest {

    private Function<Paging, Pageable<User>> usersFunction;
    private SerializeBuilder serializeBuilder;

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
    public SerializeBuilder getSerializeBuilder() {
        return serializeBuilder;
    }

    //region // Getter&Setter
    public void setAccount(Account account) {
        this.account = account;
    }

    public void setUsersFunction(Function<Paging, Pageable<User>> usersFunction) {
        this.usersFunction = usersFunction;
    }

    public void setSerializeBuilder(SerializeBuilder serializeBuilder) {
        this.serializeBuilder = serializeBuilder;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
    //endregion
}
