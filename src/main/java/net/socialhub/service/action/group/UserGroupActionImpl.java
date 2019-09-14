package net.socialhub.service.action.group;

import net.socialhub.model.group.UserGroupImpl;

public class UserGroupActionImpl implements UserGroupAction {

    private UserGroupImpl userGroup;

    public UserGroupActionImpl(UserGroupImpl userGroup) {
        this.userGroup = userGroup;
    }
}
