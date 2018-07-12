package net.socialhub.service.action.group;

import net.socialhub.model.Account;
import net.socialhub.model.group.GroupAccount;
import net.socialhub.model.service.User;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * グループアクション
 */
public class ServiceGroupAction {

    private GroupAccount groupAccount;

    public Map<Account, User> getUserMe() {
        return getGroupAccount().getAccounts().parallelStream() //
                .collect(Collectors.toMap(Function.identity(), //
                        (acc) -> acc.getAction().getUserMe()));
    }





    //<editor-fold desc="// Getter&Setter">
    public GroupAccount getGroupAccount() {
        return groupAccount;
    }

    public void setGroupAccount(GroupAccount groupAccount) {
        this.groupAccount = groupAccount;
    }
    //</editor-fold>
}
