package net.socialhub.service.misskey;

import net.socialhub.logger.Logger;
import net.socialhub.model.Account;
import net.socialhub.service.action.RequestActionImpl;

public class MisskeyRequest extends RequestActionImpl {

    private Logger log = Logger.getLogger(MisskeyRequest.class);

    public MisskeyRequest(Account account) {
        super(account);
    }
}