package net.socialhub.core.action.group;

import net.socialhub.core.model.group.UsersRequestGroupImpl;

public class UsersRequestGroupActionImpl implements UsersRequestGroupAction {

    private UsersRequestGroupImpl requestGroup;

    public UsersRequestGroupActionImpl(UsersRequestGroupImpl requestGroup) {
        this.requestGroup = requestGroup;
    }

}
