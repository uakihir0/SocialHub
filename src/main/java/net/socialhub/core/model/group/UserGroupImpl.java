package net.socialhub.core.model.group;

import net.socialhub.core.action.group.UserGroupAction;
import net.socialhub.core.action.group.UserGroupActionImpl;
import net.socialhub.core.model.Account;
import net.socialhub.core.model.User;

import java.util.Map;

public class UserGroupImpl implements UserGroup {

    private Map<Account, User> entities;

    @Override
    public UserGroupAction action() {
        return new UserGroupActionImpl(this);
    }

    //region // Getter&Setter
    @Override
    public Map<Account, User> getEntities() {
        return entities;
    }

    public void setEntities(Map<Account, User> entities) {
        this.entities = entities;
    }
    //endregion
}
