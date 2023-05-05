package net.socialhub.core.model.group;

import net.socialhub.core.model.Account;
import net.socialhub.core.model.service.User;
import net.socialhub.core.action.group.UserGroupAction;

import java.util.Map;

public interface UserGroup {

    /**
     * Return User related to Accounts
     * アカウントに紐づくユーザーを返す
     */
    Map<Account, User> getEntities();

    /**
     * Get Actions
     */
    UserGroupAction action();
}
