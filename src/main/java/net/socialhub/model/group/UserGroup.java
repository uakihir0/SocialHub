package net.socialhub.model.group;

import net.socialhub.model.Account;
import net.socialhub.model.service.User;
import net.socialhub.service.action.group.UserGroupAction;

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
