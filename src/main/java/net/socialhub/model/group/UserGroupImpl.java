package net.socialhub.model.group;

import net.socialhub.model.Account;
import net.socialhub.model.service.User;
import net.socialhub.service.action.group.UserGroupAction;
import net.socialhub.service.action.group.UserGroupActionImpl;

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
