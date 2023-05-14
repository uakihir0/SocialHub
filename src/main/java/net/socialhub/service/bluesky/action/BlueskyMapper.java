package net.socialhub.service.bluesky.action;

import bsky4j.model.bsky.actor.ActorDefsProfileViewDetailed;
import net.socialhub.core.model.Service;
import net.socialhub.core.model.User;
import net.socialhub.logger.Logger;
import net.socialhub.service.bluesky.model.BlueskyUser;

public class BlueskyMapper {

    private static final Logger logger = Logger.getLogger(BlueskyMapper.class);

    public static User user(
            ActorDefsProfileViewDetailed account,
            Service service
    ) {
        BlueskyUser user = new BlueskyUser(service);
        user.setId(account.getDid());
        user.setName(account.getDisplayName());
        return user;
    }
}
