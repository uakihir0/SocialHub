package net.socialhub.service.action.group;

import net.socialhub.model.group.UsersRequestGroupImpl;

public class UsersRequestGroupActionImpl implements UsersRequestGroupAction {

    private UsersRequestGroupImpl requestGroup;

    public UsersRequestGroupActionImpl(UsersRequestGroupImpl requestGroup) {
        this.requestGroup = requestGroup;
    }

}
