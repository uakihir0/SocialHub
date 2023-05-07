package net.socialhub.core.action.group;

import net.socialhub.core.model.group.UserGroupImpl;

public class UserGroupActionImpl implements UserGroupAction {

    private UserGroupImpl userGroup;

    public UserGroupActionImpl(UserGroupImpl userGroup) {
        this.userGroup = userGroup;
    }
}
