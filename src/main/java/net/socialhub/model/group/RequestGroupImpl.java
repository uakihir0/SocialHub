package net.socialhub.model.group;

import net.socialhub.model.Account;
import net.socialhub.service.action.RequestAction;
import net.socialhub.service.action.group.RequestGroupAction;
import net.socialhub.service.action.group.RequestGroupActionImpl;

import java.util.HashMap;
import java.util.Map;

public class RequestGroupImpl implements RequestGroup {

    /** Entity of Accounts Actions */
    private Map<Account, RequestAction> actions = new HashMap<>();

    @Override
    public void addAccount(Account account, RequestAction action) {
        actions.put(account, action);
    }

    @Override
    public RequestGroupAction action() {
        return new RequestGroupActionImpl(this);
    }

    //region // Getter&Setter
    @Override
    public Map<Account, RequestAction> getActions() {
        return actions;
    }

    public void setActions(Map<Account, RequestAction> actions) {
        this.actions = actions;
    }
    //endregion
}
