package net.socialhub.service.slack;

import net.socialhub.model.Account;
import net.socialhub.service.action.RequestActionImpl;

public class SlackRequest extends RequestActionImpl {

    public SlackRequest(Account account) {
        super(account);
    }
}
